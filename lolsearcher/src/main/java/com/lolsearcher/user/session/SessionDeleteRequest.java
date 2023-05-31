package com.lolsearcher.user.session;

import lombok.Data;

@Data
public class SessionDeleteRequest {

    private final String cutSessionId;

    public SessionDeleteRequest(){
        cutSessionId = "";
    }
}
