package com.pirate.arena.app.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException  {

    public ForbiddenException(String message) {
        super(message);
        log.warn("ForbiddenException: " + message);
    }


}