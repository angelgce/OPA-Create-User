package com.pirate.arena.app.functions;

import com.pirate.arena.app.request.RequestCreatePlayer;
import com.pirate.arena.app.request.RequestEditAttributes;
import com.pirate.arena.app.services.ServiceUser;
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
    private final ServiceUser serviceuser;

    //create a player


    @Bean
    public Function<RequestCreatePlayer, ResponseEntity<Map<String, String>>> createUser() {
        return value -> ResponseEntity.ok()
                .body(Collections.singletonMap("data", serviceuser.createPlayer(value)));
    }

    @Bean
    public Function<RequestEditAttributes, ResponseEntity<Map<String, String>>> editPlayer() {
        return value -> ResponseEntity.ok()
                .body(Collections.singletonMap("data", serviceuser.editPlayer(value)));
    }

}
