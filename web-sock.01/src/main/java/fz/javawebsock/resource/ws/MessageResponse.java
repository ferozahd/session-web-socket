package fz.javawebsock.resource.ws;

import fz.javawebsock.enums.WebSocketResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse implements Serializable {
    private WebSocketResponseStatus status;
    public String content;
    public String deliverTime;
    public String destination;
    public Set<String> activeUsers;
}
