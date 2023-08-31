package fz.javawebsock.controller;

import fz.javawebsock.entity.User;
import fz.javawebsock.repository.UserRepository;
import fz.javawebsock.resource.auth.RegisterPostResource;
import fz.javawebsock.resource.auth.TokenResponse;
import fz.javawebsock.resource.user.UserGet;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class WebSocketController {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
   @PostMapping("/register")
    ResponseEntity<TokenResponse> register(@RequestBody RegisterPostResource post){
       if(!StringUtils.hasText(post.getUserName())){
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Name must needed");
       }
       Optional<User> existedUser = userRepository.findByUserName(post.getUserName());
       if(existedUser.isPresent()){
           return ResponseEntity.ok(new TokenResponse(existedUser.get().getId().toHexString()));
       }

       User user =new User();
       user.setUserName(post.getUserName());
       user.setCreatedAt(LocalDateTime.now());
       user.setLastUpdate(LocalDateTime.now());
       return ResponseEntity.ok(new TokenResponse(userRepository.save(user).getId().toHexString()));
   }

   @GetMapping("/buddy-list/{me}")
    ResponseEntity<List<UserGet>> getAllBuddy(@PathVariable String me){
   return ResponseEntity.ok(mongoTemplate.find(Query.query(Criteria.where("_id").ne(new ObjectId(me))),User.class)
       .stream()
       .map(this::toUserGet).collect(Collectors.toList()));
   }

    private UserGet  toUserGet(User user) {
       return UserGet.builder()
           .id(user.getId().toHexString())
           .userName(user.getUserName())
           .createdAt(LocalDateTime.now())
           .lastUpdate(user.getLastUpdate())
           .build();
    }

}
