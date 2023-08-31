package fz.javawebsock.config;


import fz.javawebsock.enums.WebSocketPostType;
import fz.javawebsock.enums.WebSocketResponseStatus;
import fz.javawebsock.repository.UserRepository;
import fz.javawebsock.resource.ws.MessageResponse;
import fz.javawebsock.resource.ws.WebSocketPostResource;
import fz.javawebsock.utils.ObjectJsonUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MyWebSocketHandler extends TextWebSocketHandler {
    private final UserRepository userRepository;
    private Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketPostResource content = ObjectJsonUtils.stringToObjectClass(message.getPayload(), WebSocketPostResource.class);
        if (content.getType() != null && content.getType().equals(WebSocketPostType.JOIN)) {
            joinNewUserToChannel(session, content.getToken());
        }
    }

    private void joinNewUserToChannel(WebSocketSession session, String token) throws IOException {
        MessageResponse message = null;
        if (token == null) {
            message = MessageResponse.builder().status(WebSocketResponseStatus.FAILED)
                .deliverTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM:ss")))
                .content("No token found")
                .build();
        } else if (userRepository.findById(new ObjectId(token)).isPresent()) {
            noticeAllBuddyNewUserJoin(session, token);
        } else {
            message = MessageResponse.builder().status(WebSocketResponseStatus.FAILED).content("Bad Credentials").build();
        }

        if (Objects.nonNull(message)) {
            new EventSender(message).send(session);
        }
    }


    private void noticeAllBuddyNewUserJoin(WebSocketSession session, String token) throws IOException {
        this.sessions.put(token, session);

        this.sessions.values().stream()
            .filter(s -> !session.getId().equals(s.getId()))
            .forEach(new EventSender(MessageResponse.builder()
                .status(WebSocketResponseStatus.JOIN)
                .deliverTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM:ss")))
                .content(token).build())::send);


        new EventSender(MessageResponse.builder()
            .status(WebSocketResponseStatus.ACTIVE_USERS)
            .deliverTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM:ss")))
            .content(token)
            .activeUsers(this.sessions.keySet().stream().filter(s -> !s.equals(token)).collect(Collectors.toSet()))
            .build()).send(session);

    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        this.sessions.entrySet().stream().filter(s -> s.getValue().getId().equals(session.getId()))
            .findFirst()
            .map(Map.Entry::getKey).ifPresent(id -> {
                this.sessions.remove(id);
                MessageResponse message = MessageResponse.builder()
                    .status(WebSocketResponseStatus.LEAVE_USER)
                    .deliverTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:MM:ss")))
                    .content(id)
                    .build();
                this.sessions.values().forEach(new EventSender(message)::send);
            });
    }



    private class EventSender {
        private final MessageResponse response;

        public EventSender(MessageResponse response) {
            this.response = response;
        }

        public void send(WebSocketSession webSocketSession) {
            try {
                webSocketSession.sendMessage(new TextMessage(ObjectJsonUtils.objectToJsonString(response)));
            } catch (Exception e) {
            }
        }
    }

}
