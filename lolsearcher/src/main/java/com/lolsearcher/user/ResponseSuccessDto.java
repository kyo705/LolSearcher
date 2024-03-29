package com.lolsearcher.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ResponseSuccessDto {

    private final Boolean success;
    private final String message;

    public ResponseSuccessDto(){
        success = true;
        this.message = "";
    }
}
