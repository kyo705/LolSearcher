package com.lolsearcher.api.notification;

import com.lolsearcher.model.request.notification.RequestNotificationDto;

public interface NotificationServerApi {

    void sendNotificationMessage(RequestNotificationDto requestNotificationDto);
}
