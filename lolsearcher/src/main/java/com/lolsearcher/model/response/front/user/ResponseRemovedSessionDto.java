package com.lolsearcher.model.response.front.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class ResponseRemovedSessionDto {

    private final String requestSessionId;
    private final String removedSessionId;

    public ResponseRemovedSessionDto(){
        this.requestSessionId = "";
        this.removedSessionId = "";
    }
}
