package com.lolsearcher.model.request.user;

import lombok.Data;

@Data
public class RequestJoinIdentificationDto {

    private final int requestRandomNum;

    public RequestJoinIdentificationDto(){
        requestRandomNum = 0;
    }
}
