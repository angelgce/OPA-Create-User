package com.pirate.arena.app.models;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Profile(String avatar, String joinDate) {
}
