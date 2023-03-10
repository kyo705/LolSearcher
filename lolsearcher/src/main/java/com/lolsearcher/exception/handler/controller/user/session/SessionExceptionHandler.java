package com.lolsearcher.exception.handler.controller.user.session;

import com.lolsearcher.controller.user.UserSessionController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = UserSessionController.class)
public class SessionExceptionHandler {


}
