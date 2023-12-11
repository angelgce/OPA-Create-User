package com.pirate.arena.app.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ServiceCreateCode {

    public String getCode() {
        StringBuilder output = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++)
            output.append(rand.nextInt(9));
        return output.toString();
    }
}
