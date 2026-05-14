package com.campustrade.gateway.client;

import com.campustrade.common.constant.ServiceNames;
import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "campus.remote", name = "mode", havingValue = "rpc")
public class RpcMessageServiceClient extends BaseRemoteClient implements MessageServiceClient {
    private final GatewayRemoteProperties properties;

    public RpcMessageServiceClient(GatewayRemoteProperties properties,
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
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "listConversations", userId);
    }

    @Override
    public List<MessageDTO> listConversationMessages(Long conversationId, Long userId) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "listConversationMessages", conversationId, userId);
    }

    @Override
    public Integer unreadCount(Long userId) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "unreadCount", userId);
    }

    @Override
    public MessageDTO sendMessage(Object requestBody) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "sendMessage", requestBody);
    }
}
