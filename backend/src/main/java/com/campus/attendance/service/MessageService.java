package com.campus.attendance.service;

import com.campus.attendance.domain.NotifyMessage;

import java.util.List;

public interface MessageService {
    List<NotifyMessage> listMyMessages(Long userId);

    void readMessage(Long userId, Long messageId);

    void readAll(Long userId);
}
