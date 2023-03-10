package com.lolsearcher.model.request.user.session;

import lombok.Data;

@Data
public class RequestCutSessionDto {

    private final String cutSessionId;

    public RequestCutSessionDto(){
        cutSessionId = "";
    }
}
