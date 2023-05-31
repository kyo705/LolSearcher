package com.lolsearcher.errors.handler.controller;

import com.lolsearcher.user.session.SessionController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = SessionController.class)
public class SessionExceptionHandler {


}
