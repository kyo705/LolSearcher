package com.lolsearcher.model.output.kafka;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class RemainingMatchId {

    private final String startMatchId; /* 해당 아이디 이후부터 조회 */
    private final String endMatchId; /* 해당 아이디 이전까지 조회 */
}
