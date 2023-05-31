package com.lolsearcher.search.rank;

import com.lolsearcher.errors.exception.search.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.errors.exception.search.rank.NonUniqueRankTypeException;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.QueryTimeoutException;

import java.util.Map;
import java.util.stream.Stream;

import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_FLEX_SR;
import static com.lolsearcher.search.rank.RankTypeState.RANKED_SOLO_5x5;

public class RankSetup {

    public static Map<RankTypeState, RankDto> correctResult(String summonerId, Integer seasonId){

        return  Map.of(
                RankTypeState.RANKED_FLEX_SR, RankDto.builder()
                        .seasonId(seasonId)
                        .summonerId(summonerId)
                        .queueType(RankTypeState.RANKED_FLEX_SR)
                        .build(),
                RANKED_SOLO_5x5, RankDto.builder()
                        .seasonId(seasonId)
                        .summonerId(summonerId)
                        .queueType(RANKED_SOLO_5x5)
                        .build()
        );
    }

    public static Stream<Arguments> correctParamWithFindAll() {

        return Stream.of(
                Arguments.of("summonerId", null),
                Arguments.of("summonerId", 13),
                Arguments.of("summonerId", 10),
                Arguments.of("summonerId", CURRENT_SEASON_ID),
                Arguments.of("12345678901234567890123456789012345678901234567890123456789012", 13),   /* summonerId 길이 : 62 */
                Arguments.of("123456789012345678901234567890123456789012345678901234567890123", 13)  /* summonerId 길이 : 63 */
        );
    }

    public static Stream<Arguments> incorrectParamWithFindAll() {

        return Stream.of(
                Arguments.of("summonerId", 0),
                Arguments.of("summonerId", -1),
                Arguments.of("summonerId", 9),
                Arguments.of("summonerId", 8),
                Arguments.of("summonerId", CURRENT_SEASON_ID+1),
                Arguments.of("summonerId", CURRENT_SEASON_ID+2),
                Arguments.of("1234567890123456789012345678901234567890123456789012345678901234", 13),  /* summonerId 길이 : 64 */
                Arguments.of("12345678901234567890123456789012345678901234567890123456789012345", 13)  /* summonerId 길이 : 65 */

        );
    }

    public static Stream<Arguments> incorrectPersistenceDataWithFindAll() {

        return Stream.of(
                Arguments.of(new IncorrectSummonerRankSizeException(3)),
                Arguments.of(new NonUniqueRankTypeException(RANKED_FLEX_SR))
        );
    }

    public static Stream<Arguments> timeoutErrorWithFindAll() {

        return Stream.of(
                Arguments.of(new QueryTimeoutException("응답 시간 초과")),
                Arguments.of(new CannotAcquireLockException("락 획득 실패"))
        );
    }

    public static Stream<Arguments> correctParamWithFindById() {

        return Stream.of(
                Arguments.of("summonerId", RANKED_SOLO_5x5.name(), 13),
                Arguments.of("summonerId", RANKED_FLEX_SR.name(), 10),
                Arguments.of("summonerId", RANKED_SOLO_5x5.name(), CURRENT_SEASON_ID),
                Arguments.of("12345678901234567890123456789012345678901234567890123456789012",RANKED_SOLO_5x5.name(), 13),   /* summonerId 길이 : 62 */
                Arguments.of("123456789012345678901234567890123456789012345678901234567890123",RANKED_SOLO_5x5.name(), 13)  /* summonerId 길이 : 63 */
        );
    }

    public static Stream<Arguments> incorrectParamWithFindById() {

        return Stream.of(
                Arguments.of("summonerId",RANKED_SOLO_5x5.name(), 0),
                Arguments.of("summonerId",RANKED_SOLO_5x5.name(), -1),
                Arguments.of("summonerId",RANKED_SOLO_5x5.name(), 9),
                Arguments.of("summonerId",RANKED_SOLO_5x5.name(), 8),
                Arguments.of("summonerId",RANKED_SOLO_5x5.name(), CURRENT_SEASON_ID+1),
                Arguments.of("summonerId",RANKED_SOLO_5x5.name(), CURRENT_SEASON_ID+2),
                Arguments.of("1234567890123456789012345678901234567890123456789012345678901234",RANKED_SOLO_5x5.name(), 13),  /* summonerId 길이 : 64 */
                Arguments.of("12345678901234567890123456789012345678901234567890123456789012345",RANKED_SOLO_5x5.name(), 13), /* summonerId 길이 : 65 */
                Arguments.of("summonerId","INVALID_TYPE", CURRENT_SEASON_ID)
        );
    }
}
