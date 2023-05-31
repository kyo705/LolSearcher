package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.errors.ErrorResponseBody;
import com.lolsearcher.search.champion.ChampionController;
import com.lolsearcher.search.match.MatchController;
import com.lolsearcher.search.mostchamp.MostChampController;
import com.lolsearcher.search.rank.RankController;
import com.lolsearcher.search.summoner.SummonerController;
import com.lolsearcher.user.UserController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.Map;

import static com.lolsearcher.constant.BeanNameConstants.*;

@Order(5)
@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice(assignableTypes = {
		SummonerController.class, RankController.class, MatchController.class,
		MostChampController.class, ChampionController.class, UserController.class
})
public class GeneralExceptionHandler {

	private final Map<String, ResponseEntity<ErrorResponseBody>> errorResponseEntities;

	@ExceptionHandler({
			QueryTimeoutException.class,
			CannotAcquireLockException.class
	})
	public ResponseEntity<ErrorResponseBody> handleTimeOutException(Exception e) {

		log.error(e.getMessage());

		return errorResponseEntities.get(TIME_OUT_ENTITY_NAME);
	}

	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<ErrorResponseBody> handleExternalServerException(Exception e) {

		log.error(e.getMessage());

		return errorResponseEntities.get(BAD_GATEWAY_ENTITY_NAME);
	}

	@ExceptionHandler({
			ConstraintViolationException.class,         // pathvariable @validated 검사 실패 시
			MethodArgumentNotValidException.class,      // requestBody @valid 검사 실패 시
			ConversionFailedException.class,		    // enum type data bind 실패시
			IllegalArgumentException.class,             // custom으로 파라미터 검사 실패 시
			MethodArgumentTypeMismatchException.class,
			BindException.class,						// modelAttribute 바인딩 실패 시
			HttpMessageNotReadableException.class
	})
	public ResponseEntity<ErrorResponseBody> handleInvalidArgumentException(Exception e) {

		log.error("잘못된 파라미터 요청 " + e.getMessage());

		return errorResponseEntities.get(BAD_REQUEST_ENTITY_NAME);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseBody> handleAnyException(Exception e) {

		log.error(e.getMessage());

		return errorResponseEntities.get(INTERNAL_SERVER_ERROR_ENTITY_NAME);
	}
}
