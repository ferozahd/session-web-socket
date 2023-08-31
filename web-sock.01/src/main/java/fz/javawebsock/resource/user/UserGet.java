package fz.javawebsock.resource.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class UserGet {
    private String id ;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate ;
}
