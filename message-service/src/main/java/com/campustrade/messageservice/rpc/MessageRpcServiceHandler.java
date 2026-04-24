package com.campustrade.messageservice.rpc;

import com.campustrade.common.rpc.RpcServiceHandler;
import com.campustrade.messageservice.request.SendMessageRequest;
import com.campustrade.messageservice.service.MessageService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageRpcServiceHandler implements RpcServiceHandler {
    private final MessageService messageService;

    public MessageRpcServiceHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Object handle(String methodName, Object[] arguments) {
        return switch (methodName) {
            case "sendMessage" -> messageService.sendMessage(toSendMessageRequest(arguments[0]));
            case "listConversations" -> messageService.listConversations(toLong(arguments, 0));
            case "listConversationMessages" -> messageService.listConversationMessages(
                    toLong(arguments, 0),
                    toLong(arguments, 1)
            );
            case "unreadCount" -> messageService.unreadCount(toLong(arguments, 0));
            default -> throw new IllegalArgumentException("Unsupported message rpc method: " + methodName);
        };
    }

    private SendMessageRequest toSendMessageRequest(Object argument) {
        if (argument instanceof SendMessageRequest request) {
            return request;
        }
        if (!(argument instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Unsupported sendMessage payload type: " + argument);
        }

        SendMessageRequest request = new SendMessageRequest();
        request.setConversationId(asLong(map.get("conversationId")));
        request.setProductId(asLong(map.get("productId")));
        request.setSenderId(asLong(map.get("senderId")));
        request.setReceiverId(asLong(map.get("receiverId")));
        request.setMessageType(asString(map.get("messageType")));
        request.setContent(asString(map.get("content")));
        return request;
    }

    private Long toLong(Object[] args, int index) {
        Object value = args[index];
        if (value instanceof Number number) {
            return number.longValue();
        }
        return (Long) value;
    }

    private Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }
}
