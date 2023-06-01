package com.lolsearcher.search.rank;

import com.lolsearcher.errors.exception.rank.IncorrectSummonerRankSizeException;
import com.lolsearcher.errors.exception.rank.NonUniqueRankTypeException;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.QueryTimeoutException;

import java.util.Map;
import java.util.stream.Stream;

import static com.lolsearcher.search.rank.RankConstant.CURRENT_SEASON_ID;
import static com.lolsearcher.search.rank.RankConstant.INITIAL_SEASON_ID;
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
                Arguments.of(
                        "summonerId",
                        13,
                        RANKED_SOLO_5x5.name()
                ),
                Arguments.of(
                        "summonerId",
                        10,
                        RANKED_SOLO_5x5.name()
                ),
                Arguments.of(
                        "summonerId",
                        CURRENT_SEASON_ID,
                        RANKED_FLEX_SR.name()
                ),
                Arguments.of(
                        "12345678901234567890123456789012345678901234567890123456789012",  /* summonerId 길이 : 62 */
                        13,
                        RANKED_FLEX_SR.name()
                ),
                Arguments.of(
                        "123456789012345678901234567890123456789012345678901234567890123",
                        13,
                        RANKED_FLEX_SR.name()
                )  /* summonerId 길이 : 63 */
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

    protected static Stream<Arguments> validRanksParam() {

        return Stream.of(
                /* summonerId, seasonId */
                Arguments.of("summoner1", CURRENT_SEASON_ID),
                Arguments.of("summoner1", INITIAL_SEASON_ID),
                Arguments.of("summoner2", CURRENT_SEASON_ID),
                Arguments.of("summoner2", INITIAL_SEASON_ID)
        );
    }

    protected static Stream<Arguments> invalidRanksParam() {

        return Stream.of(
                /* summonerId, seasonId */
                Arguments.of("summoner1", CURRENT_SEASON_ID+1),
                Arguments.of("summoner1", INITIAL_SEASON_ID-1),
                Arguments.of("  ", CURRENT_SEASON_ID),
                Arguments.of("1234567890123456789012345678901234567890123456789012345678901234", INITIAL_SEASON_ID)
        );
    }

    protected static Stream<Arguments> invalidRanksInDB() {

        return Stream.of(
                /* summonerId, seasonId */
                Arguments.of("summoner3", CURRENT_SEASON_ID),
                Arguments.of("summoner4", CURRENT_SEASON_ID)
        );
    }

    protected static Stream<Arguments> validRankByIdParam() {

        return Stream.of(
                /* summonerId, seasonId, rankId */
                Arguments.of("summoner1", CURRENT_SEASON_ID, RANKED_FLEX_SR.name()),
                Arguments.of("summoner2", CURRENT_SEASON_ID, RANKED_SOLO_5x5.name())
        );
    }

    protected static Stream<Arguments> invalidRankByIdParam() {

        return Stream.of(
                /* summonerId, seasonId, rankId */
                Arguments.of(
                        "  ",
                        CURRENT_SEASON_ID,
                        RANKED_FLEX_SR.name()
                ),
                Arguments.of(
                        "1234567890123456789012345678901234567890123456789012345678901234" /* len : 64 */,
                        CURRENT_SEASON_ID,
                        RANKED_FLEX_SR.name()
                ),
                Arguments.of(
                        "summoner2",
                        CURRENT_SEASON_ID+1,
                        RANKED_SOLO_5x5.name()
                ),
                Arguments.of(
                        "summoner2",
                        INITIAL_SEASON_ID-1,
                        RANKED_SOLO_5x5.name()
                ),
                Arguments.of(
                        "summoner2",
                        -1,
                        RANKED_SOLO_5x5.name()
                ),
                Arguments.of(
                        "summonerId",
                        CURRENT_SEASON_ID,
                        "INVALID_TYPE"
                )
        );
    }

}
