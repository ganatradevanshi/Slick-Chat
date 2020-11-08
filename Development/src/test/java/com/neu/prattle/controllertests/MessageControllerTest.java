package com.neu.prattle.controllertests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.neu.prattle.controller.MessageController;
import com.neu.prattle.service.dbservice.MessageService;
import com.neu.prattle.service.dbservice.PollService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.service.mediaservice.MediaService;
import com.neu.prattle.utils.JWTUtils;
import fse.team2.common.models.controllermodels.AddPollResponseParams;
import fse.team2.common.models.controllermodels.CreateMediaMessageParams;
import fse.team2.common.models.controllermodels.CreatePollMessageParams;
import fse.team2.common.models.controllermodels.RemovePollResponseParams;
import fse.team2.common.models.mongomodels.UserModel;
import com.neu.prattle.service.dbservice.DatabaseService;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;
import fse.team2.slickclient.model.ClientMessagePojo;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MessageControllerTest {

    DatabaseService<Message> messageService;
    DatabaseService<UserModel> userService;
    MessageController msgController;
    private UserModel user1;
    private UserModel user2;
    private UserModel user3;
    private Message message1;
    private Message message2;

    private String token;
    private String token2;

    private String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Before
    public void setUp() throws Exception {
        user1 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();

        user2 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();

        user3 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();

        message1 = Message.messageBuilder()
                .setId(new ObjectId())
                .setSenderId(user1.getId())
                .setReceiverId(user2.getId())
                .setEncryptionLevel(EncrpytionLevel.NONE)
                .setMessageContent("hey there - text1")
                .build();

        message2 = Message.messageBuilder()
                .setId(new ObjectId())
                .setSenderId(user2.getId())
                .setReceiverId(user1.getId())
                .setEncryptionLevel(EncrpytionLevel.NONE)
                .setMessageContent("hey there - text2")
                .build();

        msgController = new MessageController();

    }

    private String getAuthTokenFromUserId(String userId) {
        return "Bearer " + JWTUtils.generateJWToken(userId);
    }

    // get all messages successfully - 200Ok (user1 to user2)
    @Test
    public void getAllMessagesTest() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user1.getId().toString());

        Response response = msgController.getAllMessages(user2.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());
    }

    // get all messages successfully - token not decoded
    @Test
    public void getAllMessagesFailTest() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = "garbage Token";

        Response response = msgController.getAllMessages(user2.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());
    }

    //    // get all messages successfully - 200Ok (user1 to user2 & user2 to user1)
//    @Test
    public void getAllMessagesBothWaysTest() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);
        messageService.add(message2);

        token = getAuthTokenFromUserId(user1.getId().toString());
        token2 = getAuthTokenFromUserId(user1.getId().toString());

        Response response = msgController.getAllMessages(user2.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());
        messageService.deleteById(message2.getId());
    }

    // senderId is null
    @Test
    public void getAllMessagesFailSenderIdNull() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user1.getId().toString());

        Response response = msgController.getAllMessages("529cd5686f854f1512000001", token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }

    // selfUser is null
    @Test
    public void getAllMessagesFailSelfUserNull() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user3.getId().toString());

        Response response = msgController.getAllMessages(user2.getId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }

    // Exception - forbidden
    @Test
    public void getAllMessagesException() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user2.getId().toString());

        Response response = msgController.getAllMessages(user3.getId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }

    // delete message - 200ok
    @Test
    public void deleteMessageOk() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user1.getId().toString());

        Response response = msgController.deleteMessage(message1.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }

    // delete message - token not decoded
    @Test
    public void deleteMessageTokenNotDecoded() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = "garbage Token";

        Response response = msgController.deleteMessage(message1.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }

    // delete message - senderId null
    @Test
    public void deletesenderIdNull() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user3.getId().toString());
        ;

        Response response = msgController.deleteMessage(message1.getId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }

    // delete message - messageId null
    @Test
    public void deletemsgIdNull() {
        userService = UserService.getInstance();
        messageService = MessageService.getInstance();

        userService.add(user1);
        userService.add(user2);
        messageService.add(message1);

        token = getAuthTokenFromUserId(user1.getId().toString());
        ;

        Response response = msgController.deleteMessage("529cd5686f854f1512000001", token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        userService.deleteById(user1.getId());
        userService.deleteById(user2.getId());
        messageService.deleteById(message1.getId());

    }


    @Test
    public void mediaMessageCreationTest() {
        DatabaseService<Message> messageService = MessageService.getInstance();
        MediaService mediaService = mock(MediaService.class);
        MessageController controller = new MessageController(messageService, mediaService);
        CreateMediaMessageParams params = new CreateMediaMessageParams();
        params.setFileName("test.png");
        params.setFileContents("abc");
        params.setReceiverId(new ObjectId().toString());
        params.setSenderId(new ObjectId().toString());
        String url = "http://test.png";

        when(mediaService.upload(any(), any())).thenReturn(url);
        Response response = controller.createMediaMessage(params);
        Gson gson = new Gson();
        ClientMessagePojo messagePojo = gson.fromJson(response.readEntity(String.class), ClientMessagePojo.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(url, messagePojo.getContent());
    }

    @Test
    public void pollAPIsTest() {
        DatabaseService<Message> messageService = MessageService.getInstance();
        MessageController controller = new MessageController();
        CreatePollMessageParams params = new CreatePollMessageParams();
        params.setSenderId(new ObjectId().toString());
        params.setReceiverId(new ObjectId().toString());
        params.setPollQuestion("Yes or no question");
        List<String> options = new ArrayList<>();
        options.add("yes");
        options.add("no");
        params.setOptions(options);

        Response response = controller.createPollMessage(params);
        Gson gson = new Gson();
        ClientMessagePojo message = gson.fromJson(response.readEntity(String.class), ClientMessagePojo.class);

        ObjectId messageId = new ObjectId(message.getId());
        Response response2 = controller.getPoll(messageId.toString());

        AddPollResponseParams params1 = new AddPollResponseParams();
        params1.setMessageId(messageId.toString());
        params1.setResponse("yes");
        params1.setUserId(params.getReceiverId());

        Response response3 = controller.addPollResponse(params1);

        RemovePollResponseParams params2 = new RemovePollResponseParams();
        params2.setMessageId(messageId.toString());
        params2.setUserId(params.getReceiverId());

        Response response4 = controller.removePollResponse(params2);

        messageService.deleteById(messageId);
        PollService.getInstance().deleteById(messageId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response2.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response3.getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), response4.getStatus());
    }

}
