package com.pirate.arena.app.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

public interface IServiceDynamoDB {

    Item getItemByKey(String tableName, String key, String value);

    List<Item> getItemByIndex(String tableName, String key, String value, String indexKey);

    int updateItem(String tableName, String primaryKey, String primaryKeyValue, String key, String value);

    void putItem(String tableName, Item item);
    void deleteItem(String tableName, String key, String value);


}
