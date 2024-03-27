package com.pirate.arena.app.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.pirate.arena.app.request.RequestEditAttributes;

import java.util.List;
import java.util.Optional;

public interface IServiceQueries {

    //Users
    Optional<Item> getUserByEmail(String email);

    Optional<List<Item>> getUserByUsername(String username);

    void addUser(Item item);

    void editUser(String email, String key, String value);

    //Codes
    void validateCode(RequestEditAttributes request);

    void deleteCode(RequestEditAttributes request);

}
