package com.campustrade.messageservice.controller;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.common.result.Result;
import com.campustrade.messageservice.request.SendMessageRequest;
import com.campustrade.messageservice.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public Result<MessageDTO> send(@Valid @RequestBody SendMessageRequest request) {
        return Result.success(messageService.sendMessage(request));
    }

    @GetMapping("/conversations")
    public Result<List<ConversationDTO>> conversations(@RequestParam Long userId) {
        return Result.success(messageService.listConversations(userId));
    }

    @GetMapping("/conversations/{conversationId}")
    public Result<List<MessageDTO>> conversationMessages(@PathVariable Long conversationId,
                                                         @RequestParam Long userId) {
        return Result.success(messageService.listConversationMessages(conversationId, userId));
    }

    @GetMapping("/unread-count")
    public Result<Integer> unreadCount(@RequestParam Long userId) {
        return Result.success(messageService.unreadCount(userId));
    }
}
