package com.lolsearcher.user.session;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SessionDeleteRequest {

    private final String cutSessionId;

    public SessionDeleteRequest(){
        cutSessionId = "";
    }
}
