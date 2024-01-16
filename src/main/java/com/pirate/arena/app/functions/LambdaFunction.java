package com.pirate.arena.app.functions;

import com.pirate.arena.app.models.Request;
import com.pirate.arena.app.services.ServiceCreateUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class LambdaFunction {
    private final ServiceCreateUser serviceCreateUser;

    @Bean
    public Function<Request, ResponseEntity<Map<String, String>>> test() {
        return value -> ResponseEntity.ok()
                .body(Collections.singletonMap("data", serviceCreateUser.createUser(value)));
    }

}
