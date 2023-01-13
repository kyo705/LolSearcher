package com.lolsearcher.exception.handler.summoner;

import com.lolsearcher.controller.match.MatchController;
import com.lolsearcher.controller.rank.RankController;
import com.lolsearcher.controller.summoner.SummonerController;
import com.lolsearcher.model.output.common.ErrorResponseBody;
import com.lolsearcher.service.ban.SearchIpBanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.persistence.QueryTimeoutException;
import javax.servlet.ServletRequest;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;

@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(assignableTypes = {SummonerController.class, RankController.class, MatchController.class})
public class SummonerExceptionHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;
	private final SearchIpBanService searchIpBanService;
	
	@ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponseBody> getResponseError(WebClientResponseException e, ServletRequest request) {

		log.error("'{}' error occurred by 'Riot' game server", e.getStatusCode().value());

		if (e.getStatusCode() == HttpStatus.BAD_GATEWAY ||
				e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR ||
				e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE ||
				e.getStatusCode() == HttpStatus.GATEWAY_TIMEOUT
		) {
			log.error("라이엇 게임 서버에서 에러가 발생");
			log.error(e.getMessage());
			return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
		}

		String userIp = request.getRemoteAddr();
		if (checkIpBan(userIp)) {
			log.error("접근 권한이 없는 ip 주소");
			return errorResponseEntities.get(FORBIDDEN_ENTITY_NAME);
		}

		if (e.getStatusCode().equals(HttpStatus.NOT_FOUND) ||
				e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {

			log.error("클라이언트의 요청에 해당하는 소환사 정보가 없음");
			return errorResponseEntities.get(NOT_FOUND_ENTITY_NAME);
		} else if (e.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {

			log.error("너무 많은 API 요청이 들어옴");
			return errorResponseEntities.get(TOO_MANY_REQUESTS_ENTITY_NAME);
		}

		log.error("해당 서버에서 RIOT GAMES API 설정이 잘못됨");
		return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
	}

	@ExceptionHandler({DataIntegrityViolationException.class, QueryTimeoutException.class})
	public ResponseEntity<ErrorResponseBody> getResponseError(Exception e) {

		log.error("DB에 대한 문제 발생");
		log.error(e.getMessage());
		return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ErrorResponseBody> getResponseError(MethodArgumentNotValidException e) {

		log.error("잘못된 파라미터 요청");
		log.error(e.getMessage());

		return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
	}

	private boolean checkIpBan(String userIp) {
		if(searchIpBanService.isExceedBanCount(userIp)) {
			searchIpBanService.registerBanList(userIp);
			log.error(" 어뷰저라 판단하여 ip '{}' 을 차단", userIp);

			return true;
		}
		return false;
	}
}
