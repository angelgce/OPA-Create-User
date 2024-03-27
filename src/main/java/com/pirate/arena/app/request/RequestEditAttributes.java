package com.pirate.arena.app.request;

import com.pirate.arena.app.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestEditAttributes {

    private String email;
    private String attribute;
    private String value;
    private String code;

    public void isAttributeAllowed() {
        boolean isAllowed = switch (attribute) {
            case "avatar", "password" -> true;
            default -> false;
        };
        if (!isAllowed)
            throw new BadRequestException("[Error editing player] Change Not Allowed [".concat(attribute)
                    .concat("] Forbidden action [".concat(toString()).concat("]")));
    }


}
