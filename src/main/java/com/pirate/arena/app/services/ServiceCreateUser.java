package com.pirate.arena.app.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.models.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class ServiceCreateUser {

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    private final DynamoDB dynamoDB = new DynamoDB(client);
    private final ServiceCreateCode serviceCreateCode;
    private final ServiceMail serviceMail;

    public String createUser(Request request) {
        validateInputs(request);
        isUsernameInUse(request.username());
        isEmailInUse(request.email());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Table table = dynamoDB.getTable("users");
        String code = serviceCreateCode.getCode();
        table.putItem(new Item()
                .withPrimaryKey("email", request.email())
                .withString("username", request.username())
                .withString("password", passwordEncoder.encode(request.password()))
                .withString("code", code)
                .with("creationDate", LocalDateTime.now().toString())
                .withBoolean("verified", false)
        );
        serviceMail.sendWelcomeMail(request, code);
        return request.username().concat(" created successfully");
    }

    private void isUsernameInUse(String username) {
        Table table = dynamoDB.getTable("users");
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("username = :v_id")
                .withValueMap(new ValueMap()
                        .withString(":v_id", username));
        ItemCollection<QueryOutcome> items = table.getIndex("username-index").query(spec);
        Iterator<Item> iterator = items.iterator();
        if (iterator.hasNext()) throw new BadRequestException("Username " + username + " is already in use");
    }

    private void isEmailInUse(String email) {
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("email", email);
        if (item != null) throw new BadRequestException("Email " + email + " is already in use");
    }

    private void validateInputs(Request request) {
        if (request.email() == null || request.password() == null || request.username() == null)
            throw new BadRequestException("Error in the request, some mandatory fields are missing "
                    .concat(request.toString()));
        if (!request.email().matches("^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"))
            throw new BadRequestException("Error in the request, email is not valid");
        if (!request.password().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}"))
            throw new BadRequestException("Error in the request, password is not valid. [Minimum eight characters, at least one uppercase letter, one lowercase letter, one number and one special character]");
        if (!request.username().matches("^[a-zA-Z0-9]([._](?![._])|[a-zA-Z0-9]){6,18}[a-zA-Z0-9]$"))
            throw new BadRequestException("Error in the request, username is not valid. [Minimum 6 characters, maximum 18 characters, no special characters except ._]");
    }

}
