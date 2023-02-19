package com.lolsearcher.exception.exception.join;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class JoinAuthenticationException extends RuntimeException {

    private final Exception causedException;

}
