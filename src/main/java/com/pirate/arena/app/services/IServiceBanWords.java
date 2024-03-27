package com.pirate.arena.app.services;

import java.util.Set;

public interface IServiceBanWords {
    Set listOfBannedWords();

    boolean isForbiddenWord(String username);
}
