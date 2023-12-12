package com.pirate.arena.app.services;

import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.models.Request;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;


@Service
@RequiredArgsConstructor
public class ServiceMail {

    private final ServiceCreateCode serviceCreateCode;

    private String getHTMLBody() {
        StringBuilder body = new StringBuilder("<html><body><div style=\"justify-content: center; width: 95%\"><h1 style=\"text-transform: uppercase; text-align: center\"><img src=\"https://onepice-arena-resources.s3.amazonaws.com/website/icon.png\"style=\"width: 40px;  margin-right: 10px;\" />Welcome to <br> One Piece Arena</h1><p style=\"text-align: center\">Yor validation code is:</p><div style=\"text-align: center; margin-bottom: 20px;\">");
        for (String number : serviceCreateCode.getCode().split("")) {
            body.append("<span style=\"font-size: 30px; padding: 9px; border: 1px solid black; text-align: center; margin: 1px; border-radius: 5px; background-color: rgba(209, 212, 212, 0.207);\">" + number + "</span>");
        }
        body.append("</div></div></body></html>");
        return body.toString();
    }

    public String verifyEmail(Request user) {
        validateInputs(user);
        try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                            .withRegion(Regions.US_EAST_1).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(user.email()))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset("UTF-8").withData(getHTMLBody())))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData("Welcome "
                                            .concat(user.username()
                                                    .concat(" to One Piece Arena!!!")))))
                    .withSource("no-replay@onepiece-arena.com");
            client.sendEmail(request);
            System.out.println("Email sent!");
            return "Email sent!";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error " + ex.getMessage();
        }
    }

    private void validateInputs(Request user) {
        if (user.email() == null || user.username() == null)
            throw new BadRequestException("Error: Some fields are missing... " + user.toString());
        if (user.username().length() > 12 || user.username().length() < 6)
            throw new BadRequestException("Error: Username must be between 6 and 12 characters long...");
        else if (!user.username().matches("^[a-zA-Z0-9]*$"))
            throw new BadRequestException("Error: Username must contain only letters and numbers...");
        else if (!user.email().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))
            throw new BadRequestException("Error: Invalid email...");
    }

}
