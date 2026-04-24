package com.campustrade.gateway.client;

import com.campustrade.common.dto.message.ConversationDTO;
import com.campustrade.common.dto.message.MessageDTO;
import com.campustrade.common.constant.ServiceNames;
import com.campustrade.gateway.config.GatewayRemoteProperties;
import com.campustrade.gateway.rpc.GatewayRpcServiceLocator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageServiceClient extends BaseRemoteClient {
    private final GatewayRemoteProperties properties;

    public MessageServiceClient(GatewayRemoteProperties properties,
                                GatewayRpcServiceLocator serviceLocator) {
        super(serviceLocator);
        this.properties = properties;
    }

    private GatewayRemoteProperties.ServiceEndpoint endpoint() {
        return properties.getMessageService();
    }

    public List<ConversationDTO> listConversations(Long userId) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "listConversations", userId);
    }

    public List<MessageDTO> listConversationMessages(Long conversationId, Long userId) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "listConversationMessages", conversationId, userId);
    }

    public Integer unreadCount(Long userId) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "unreadCount", userId);
    }

    public MessageDTO sendMessage(Object requestBody) {
        return callRpc(ServiceNames.MESSAGE_SERVICE, endpoint(), "sendMessage", requestBody);
    }
}
