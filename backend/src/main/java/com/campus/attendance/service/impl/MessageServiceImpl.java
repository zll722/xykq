package com.campus.attendance.service.impl;

import com.campus.attendance.domain.NotifyMessage;
import com.campus.attendance.mapper.NotifyMessageMapper;
import com.campus.attendance.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    private final NotifyMessageMapper notifyMessageMapper;

    public MessageServiceImpl(NotifyMessageMapper notifyMessageMapper) {
        this.notifyMessageMapper = notifyMessageMapper;
    }

    @Override
    public List<NotifyMessage> listMyMessages(Long userId) {
        return notifyMessageMapper.listByUserId(userId);
    }

    @Override
    public void readMessage(Long userId, Long messageId) {
        notifyMessageMapper.markRead(messageId, userId);
    }

    @Override
    public void readAll(Long userId) {
        notifyMessageMapper.markAllRead(userId);
    }
}
