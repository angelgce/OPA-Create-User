package com.pirate.arena.app.services;

import com.google.gson.Gson;
import com.pirate.arena.app.request.RequestCreatePlayer;
import com.pirate.arena.app.request.RequestEditAttributes;

import java.util.Optional;

public interface IServiceUser {


    String createPlayer(RequestCreatePlayer requestCreatePlayer);

    String editPlayer(RequestEditAttributes requestEditAttributes);

    String validateAvatar(String avatar);

    String validateEmail(String email);

    String validateUsername(String username);

    String validatePassword(String password);

    String convertToJson(Optional<?> object);

    void changePassword(RequestEditAttributes request);

    void changeAttribute(RequestEditAttributes request);
}
