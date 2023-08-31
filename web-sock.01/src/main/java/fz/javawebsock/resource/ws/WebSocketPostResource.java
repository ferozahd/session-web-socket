package fz.javawebsock.resource.ws;

import fz.javawebsock.enums.WebSocketPostType;
import lombok.Data;

@Data
public class WebSocketPostResource {
//    For Private
    private String sendTo ;
    private WebSocketPostType type ; // JOIN/PRIVATE_CHAT/PUBLIC_CHAT
    private String content ;
    private String token ; // use when type.equal(JOIN)
//    private String channelId;// channel will use later

}
