package com.campustrade.gateway.controller;

import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.common.result.Result;
import com.campustrade.gateway.request.SendMessageCommand;
import com.campustrade.gateway.security.CurrentUserId;
import com.campustrade.gateway.service.MessageAggregateService;
import com.campustrade.gateway.vo.MessageInboxVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/messages")
public class MessageAggregateController {
    private final MessageAggregateService messageAggregateService;

    public MessageAggregateController(MessageAggregateService messageAggregateService) {
        this.messageAggregateService = messageAggregateService;
    }

    @GetMapping
    public Result<MessageInboxVO> myInbox(@CurrentUserId Long userId) {
        return Result.success(messageAggregateService.myInbox(userId));
    }

    @GetMapping("/conversations/{conversationId}")
    public Result<List<MessageDTO>> conversationDetail(
            @PathVariable Long conversationId,
            @CurrentUserId Long userId
    ) {
        return Result.success(messageAggregateService.conversationMessages(conversationId, userId));
    }

    @PostMapping("/send")
    public Result<MessageDTO> send(
            @CurrentUserId Long userId,
            @Valid @RequestBody SendMessageCommand request
    ) {
        return Result.success(messageAggregateService.sendMessage(userId, request));
    }
}
