package com.lolsearcher.exception.handler.search;

import com.lolsearcher.controller.search.match.MatchController;
import com.lolsearcher.controller.search.mostchamp.MostChampController;
import com.lolsearcher.controller.search.rank.RankController;
import com.lolsearcher.controller.search.stats.ChampionController;
import com.lolsearcher.controller.search.summoner.SummonerController;
import com.lolsearcher.exception.exception.common.IncorrectGameVersionException;
import com.lolsearcher.model.response.error.ErrorResponseBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.QueryTimeoutException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.BAD_GATEWAY_ENTITY_NAME;
import static com.lolsearcher.constant.BeanNameConstants.BAD_REQUEST_ENTITY_NAME;

@Order(5)
@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(assignableTypes = {
		SummonerController.class, RankController.class, MatchController.class,
		MostChampController.class, ChampionController.class
})
public class SearchExceptionHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

	@ExceptionHandler({DataIntegrityViolationException.class, QueryTimeoutException.class})
	public ResponseEntity<ErrorResponseBody> handleDatabaseException(Exception e) {

		log.error("DB에 대한 문제 발생");
		log.error(e.getMessage());

		return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, IncorrectGameVersionException.class})
	public ResponseEntity<ErrorResponseBody> handleInvalidArgumentException(Exception e) {

		log.error("잘못된 파라미터 요청");
		log.error(e.getMessage());

		return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
	}
}
