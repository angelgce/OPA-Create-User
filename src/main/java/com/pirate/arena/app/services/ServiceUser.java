package com.pirate.arena.app.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.pirate.arena.app.dynamoDB.ServiceQueries;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.models.Profile;
import com.pirate.arena.app.models.Stats;
import com.pirate.arena.app.request.RequestCreatePlayer;
import com.pirate.arena.app.request.RequestEditAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceUser extends ServiceValidateRequest implements IServiceUser {
    private final ServiceQueries serviceQueries;
    private final ServiceBanWords serviceBanWords;

    @Override
    public String createPlayer(RequestCreatePlayer requestCreatePlayer) {
        validateInputs(Optional.ofNullable(requestCreatePlayer));
        Optional<Item> optionalEmail = serviceQueries.getUserByEmail(requestCreatePlayer.email());
        if (optionalEmail.isPresent())
            throw new BadRequestException("[Email Already Exists] ".concat(requestCreatePlayer.toString()));
        Optional<List<Item>> optionalUser = serviceQueries.getUserByUsername(requestCreatePlayer.username());
        if (optionalUser.isPresent() && optionalUser.get().size() > 0)
            throw new BadRequestException("[Username Already Exists] ".concat(requestCreatePlayer.toString()));
        Item user = new Item()
                .withPrimaryKey("email", validateEmail(requestCreatePlayer.email()))
                .withString("password", validatePassword(requestCreatePlayer.password()))
                .withString("username", validateUsername(requestCreatePlayer.username()))
                .withJSON("profile", convertToJson(Optional.ofNullable(
                        Profile.builder()
                                .avatar(validateAvatar(requestCreatePlayer.avatar()))
                                .joinDate(LocalDateTime.now().toString())
                                .build())
                )).withJSON("stats",
                        convertToJson(Optional.ofNullable(new Stats(0, 0, 0))));
        serviceQueries.addUser(user);
        log.info("[User Created] ".concat(user.toString()));
        return "success";
    }

    @Override
    public String editPlayer(RequestEditAttributes request) {
        validateInputs(Optional.ofNullable(request));
        request.isAttributeAllowed();
        switch (request.getAttribute()) {
            case "password" -> changePassword(request);
            case "avatar" -> changeAttribute(request);
        }
        return "success";
    }

    //validations ->
    @Override
    public String validateAvatar(String avatar) {
        if (!avatar.startsWith("http://") && !avatar.startsWith("https://"))
            throw new BadRequestException("[Error creating player] Avatar not valid [".concat(avatar).concat("]"));
        return avatar;
    }

    @Override
    public String validateEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"))
            throw new BadRequestException("[Error creating player] Email is not valid [".concat(email).concat("]"));
        return email;
    }

    @Override
    public String validateUsername(String username) {
        if (username.length() < 5 || username.length() > 18)
            throw new BadRequestException("Error creating player] Username is not valid. Minimum 6 characters, maximum 18 characters, no special characters except ._ Request:[".concat(username).concat("]"));
        Arrays.asList(username.split(" ")).forEach(item -> {
            if (serviceBanWords.isForbiddenWord(item))
                throw new BadRequestException("Error creating player] Forbidden word ["
                        .concat(item).concat("] Request:[").concat(username).concat("]"));
        });
        return username;
    }

    @Override
    public String validatePassword(String password) {
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}"))
            throw new BadRequestException("Error creating player], Password is not valid. Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character.");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @Override
    public String convertToJson(Optional<?> object) {
        Gson gson = new Gson();
        return gson.toJson(object.get());
    }

    @Override
    public void changePassword(RequestEditAttributes request) {
        serviceQueries.getUserByEmail(request.getEmail()).orElseThrow(()
                -> new BadRequestException("[Error editing player] user not founded. Request: [".concat(request.toString().concat("]"))));
        serviceQueries.validateCode(request);
        String password = validatePassword(request.getValue());
        serviceQueries.deleteCode(request);
        serviceQueries.editUser(request.getEmail(), "password", password);
    }

    @Override
    public void changeAttribute(RequestEditAttributes request) {
        if (request.getAttribute().equals("avatar")) validateAvatar(request.getValue());
        serviceQueries.editUser(request.getEmail(), request.getAttribute(), request.getValue());
    }
}
