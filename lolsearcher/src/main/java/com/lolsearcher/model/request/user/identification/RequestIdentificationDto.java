package com.lolsearcher.model.request.user.identification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class RequestIdentificationDto {

    private final int certificationNumber;

    public RequestIdentificationDto(){
        certificationNumber = 0;
    }
}
