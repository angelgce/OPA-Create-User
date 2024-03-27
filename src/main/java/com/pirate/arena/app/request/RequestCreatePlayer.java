package com.pirate.arena.app.request;

public record RequestCreatePlayer(String email, String username, String password, String avatar) {
}
