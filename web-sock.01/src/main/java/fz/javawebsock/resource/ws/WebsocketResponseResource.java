package fz.javawebsock.resource.ws;

import fz.javawebsock.enums.WebSocketResponseStatus;
import lombok.Data;
import org.springframework.web.socket.WebSocketMessage;

@Data
public class WebsocketResponseResource<T> implements WebSocketMessage<T> {
    private T payload;
    public int payloadLength;
    private boolean last;

}
