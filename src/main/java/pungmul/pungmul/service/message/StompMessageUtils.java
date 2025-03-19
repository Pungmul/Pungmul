package pungmul.pungmul.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StompMessageUtils {

    public String serializeContent(Map<String, Object> contentMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());  // JavaTimeModule 등록
            return objectMapper.writeValueAsString(contentMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Content serialization failed", e);
        }
    }
}
