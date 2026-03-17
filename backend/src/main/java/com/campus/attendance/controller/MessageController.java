package com.campus.attendance.controller;

import com.campus.attendance.common.ApiResponse;
import com.campus.attendance.domain.NotifyMessage;
import com.campus.attendance.service.MessageService;
import com.campus.attendance.util.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<List<NotifyMessage>> list() {
        return ApiResponse.ok(messageService.listMyMessages(SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> read(@PathVariable Long id) {
        messageService.readMessage(SecurityUtils.getCurrentUserId(), id);
        return ApiResponse.ok(null);
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> readAll() {
        messageService.readAll(SecurityUtils.getCurrentUserId());
        return ApiResponse.ok(null);
    }
}
