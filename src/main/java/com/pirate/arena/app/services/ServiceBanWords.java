package com.pirate.arena.app.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Service
public class ServiceBanWords implements IServiceBanWords {

    private Set listOfBannedWords = listOfBannedWords();

    @Override
    public Set listOfBannedWords() {
        Set list = new HashSet<>();
        try {
            File file = new File("src/main/resources/BanList.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                list.add(scanner.next().trim().toLowerCase());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean isForbiddenWord(String username) {
        return listOfBannedWords.stream()
                .filter(filter -> filter.toString().contains(username.toLowerCase()))
                .findAny()
                .isPresent();
    }
}
