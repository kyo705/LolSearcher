package com.lolsearcher.notification;

import com.lolsearcher.user.ResponseSuccessDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import static com.lolsearcher.notification.NotificationConstant.NOTIFICATION_SERVER_IDENTIFICATION_URI;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebClientNotificationServerApi implements NotificationServerApi {

    private final WebClient notificationWebClient;
    private final NotificationFailureHandler failureHandler;

    @Override
    public void sendNotificationMessage(RequestNotificationDto requestNotificationDto) {

        notificationWebClient.post()
                .uri(NOTIFICATION_SERVER_IDENTIFICATION_URI)
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .body(BodyInserters.fromValue(requestNotificationDto))
                .retrieve()
                .bodyToMono(ResponseSuccessDto.class)
                .doOnError(exception -> {
                    log.error(exception.getMessage());
                    failureHandler.handle(exception);
                })
                .subscribe();
    }
}
