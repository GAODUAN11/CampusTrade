package com.campustrade.gateway.client;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class MessageServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public MessageServiceClient(GatewayRemoteProperties properties,
                                RestTemplate restTemplate,
                                ObjectMapper objectMapper,
                                GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getMessageService();
    }

    public List<ConversationDTO> listConversations(Long userId) {
        if (!useRpc()) {
            return callHttpForType(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/messages/conversations",
                    null,
                    Map.of(),
                    Map.of("userId", userId),
                    new TypeReference<List<ConversationDTO>>() {
                    }
            );
        }
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "listConversations", userId);
    }

    public List<MessageDTO> listConversationMessages(Long conversationId, Long userId) {
        if (!useRpc()) {
            return callHttpForType(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/messages/conversations/{conversationId}",
                    null,
                    Map.of("conversationId", conversationId),
                    Map.of("userId", userId),
                    new TypeReference<List<MessageDTO>>() {
                    }
            );
        }
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "listConversationMessages", conversationId, userId);
    }

    public Integer unreadCount(Long userId) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.GET,
                    endpoint(),
                    "/api/messages/unread-count",
                    null,
                    Map.of(),
                    Map.of("userId", userId),
                    Integer.class
            );
        }
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "unreadCount", userId);
    }

    public MessageDTO sendMessage(Object requestBody) {
        if (!useRpc()) {
            return callHttpForObject(
                    HttpMethod.POST,
                    endpoint(),
                    "/api/messages/send",
                    requestBody,
                    MessageDTO.class
            );
        }
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "sendMessage", requestBody);
    }
}
