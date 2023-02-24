package com.lolsearcher.unit.service.ban;

import com.lolsearcher.service.ban.IpBanService;
import com.lolsearcher.service.ban.LoginIpBanService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static com.lolsearcher.constant.LolSearcherConstants.LOGIN_BAN_COUNT;
import static com.lolsearcher.constant.RedisCacheNameConstants.LOGIN_BAN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoginBanServiceUnitTest {

    @Mock private Cache cache;
    @Mock private CacheManager cacheManager;
    private IpBanService ipBanService;

    @BeforeEach
    void setup(){
        given(cacheManager.getCache(LOGIN_BAN)).willReturn(cache);

        ipBanService = new LoginIpBanService(cacheManager);
    }

    @DisplayName("특정 ip 주소를 카운팅한다.")
    @Test
    public void addBanCount(){

        //given
        String ipAddress = "ip";
        given(cache.get(ipAddress, Integer.class)).willReturn(0).willReturn(0);

        //when
        ipBanService.addBanCount(ipAddress);

        //then
        verify(cache, times(1)).get(ipAddress, Integer.class);
        verify(cache, times(1)).putIfAbsent(ipAddress, 0);
    }

    @DisplayName("특정 ip 주소의 값이 로그인 제한 횟수를 초과할 경우 true를 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {LOGIN_BAN_COUNT, LOGIN_BAN_COUNT+1, Integer.MAX_VALUE})
    public void isExceedBanCountWithExceed(int value){

        //given
        String ipAddress = "ip";
        given(cache.get(ipAddress)).willReturn(() -> value);
        given(cache.get(ipAddress, Integer.class)).willReturn(value);

        //when
        boolean result = ipBanService.isExceedBanCount(ipAddress);

        //then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("특정 ip 주소의 값이 로그인 제한 횟수를 초과하지 않을 경우 false를 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {LOGIN_BAN_COUNT-1, 0})
    public void isExceedBanCountWithNotExceed(int value){

        //given
        String ipAddress = "ip";
        given(cache.get(ipAddress)).willReturn(() -> value);
        given(cache.get(ipAddress, Integer.class)).willReturn(value);

        //when
        boolean result = ipBanService.isExceedBanCount(ipAddress);

        //then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("캐시에 저장된 값이 유효하지 않은 데이터일 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "유효하지 않음"})
    public void isExceedBanCountWithInvalidCacheData(String value){

        //given
        String ipAddress = "ip";
        given(cache.get(ipAddress)).willReturn(() -> value);
        given(cache.get(ipAddress, Integer.class)).willThrow(IllegalStateException.class);

        //when & then
        assertThrows(RuntimeException.class, ()->ipBanService.isExceedBanCount(ipAddress));
    }
}
