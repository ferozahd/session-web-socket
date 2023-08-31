package fz.javawebsock.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "user")
@Data
public class User {
    @Id
    private ObjectId id ;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdate ;

}
