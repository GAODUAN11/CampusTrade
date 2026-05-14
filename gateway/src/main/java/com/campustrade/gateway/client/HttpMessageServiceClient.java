package com.campustrade.gateway.client;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(
        prefix = "campus.remote",
        name = "mode",
        havingValue = "http",
        matchIfMissing = true
)
public class HttpMessageServiceClient extends BaseRemoteClient implements MessageServiceClient {
    private final GatewayRemoteProperties properties;

    public HttpMessageServiceClient(GatewayRemoteProperties properties,
                                    RestTemplate restTemplate,
                                    ObjectMapper objectMapper,
                                    GatewayRpcServiceLocator serviceLocator) {
        super(properties, restTemplate, objectMapper, serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getMessageService();
    }

    @Override
    public List<ConversationDTO> listConversations(Long userId) {
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

    @Override
    public List<MessageDTO> listConversationMessages(Long conversationId, Long userId) {
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

    @Override
    public Integer unreadCount(Long userId) {
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

    @Override
    public MessageDTO sendMessage(Object requestBody) {
        return callHttpForObject(
                HttpMethod.POST,
                endpoint(),
                "/api/messages/send",
                requestBody,
                MessageDTO.class
        );
    }
}
