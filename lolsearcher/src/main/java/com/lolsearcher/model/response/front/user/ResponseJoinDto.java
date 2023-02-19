package com.lolsearcher.model.response.front.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ResponseJoinDto {

    private final String message;
}
