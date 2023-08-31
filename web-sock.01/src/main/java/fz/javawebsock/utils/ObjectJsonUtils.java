package fz.javawebsock.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fz.javawebsock.resource.ws.MessageResponse;

public class ObjectJsonUtils {

    public static <T> T stringToObjectClass(String payload, Class<T> obj) throws JsonProcessingException {
        return  new ObjectMapper().readValue(payload,obj);
    }

    public static String objectToJsonString(MessageResponse message) throws JsonProcessingException {
        return new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(message);
    }
//    public static  <T> Object stringToObjectClass(String payload, T obj) throws JsonProcessingException {
//        return  new ObjectMapper().readValue(payload,T);
//    }
}
