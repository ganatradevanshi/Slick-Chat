package fse.team2.slickclient.services;

import com.google.gson.Gson;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.websockets.commands.ClientCommand;
import fse.team2.common.websockets.commands.ServerCommand;
import fse.team2.common.websockets.commands.enums.ServerCommandType;
import fse.team2.slickclient.model.ClientUserPojo;
import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import fse.team2.slickclient.commands.Command;
import fse.team2.slickclient.constants.AppConstants;
import fse.team2.slickclient.controller.CommandControllerImpl;
import fse.team2.slickclient.model.ClientMessagePojo;
import fse.team2.slickclient.model.ClientUserImpl;
import fse.team2.slickclient.view.CommandView;
import fse.team2.slickclient.view.CommandViewImpl;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.ws.rs.client.Client;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.*;

public class UserServicesTest {
    private Command abstractCommand;
    private CommandView commandView;
    private StringBuilder log;

    private HttpService httpServiceMock;
    private String username;

    @Before
    public void init() {
        username = "testuser";
        this.log = new StringBuilder();
        this.commandView = new CommandViewImpl();
        httpServiceMock = mock(HttpService.class);

        String user = "{\n" +
                "    \"id\": \"" + new ObjectId() + "\",\n" +
                "    \"username\": \"testuser\",\n" +
                "    \"name\": \"root\",\n" +
                "    \"deleted\": false,\n" +
                "    \"hidden\": false,\n" +
                "    \"preferences\": [],\n" +
                "    \"followers\": [],\n" +
                "    \"following\": [],\n" +
                "    \"messages\": [],\n" +
                "    \"groups\": []\n" +
                "}";


        Response postResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create(user, MediaType.parse(user)))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendPOST(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(postResponse);

        String users = "[" + user + "]";
        Response searchUsersResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create(users, MediaType.parse(users)))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendGET(anyString(), Matchers.any())).thenReturn(searchUsersResponse);

        when(httpServiceMock.sendPUT(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(postResponse);
    }

    @After
    public void tearDown() {
        Mockito.reset(httpServiceMock);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new ClientUserImpl());
    }

    @Test
    public void singleTonTest() {
        UserService service1 = UserServiceImpl.getInstance(httpServiceMock);
        UserService service2 = UserServiceImpl.getInstance(httpServiceMock);
        assertEquals(service1, service2);

        MessageService messageService1 = MessageServiceImpl.getInstance(httpServiceMock);
        MessageService messageService2 = MessageServiceImpl.getInstance(httpServiceMock);
        assertEquals(messageService1, messageService2);
    }

    /* ********** Implementation Model Tests ********** */
    @Test
    public void testHelp() {
        String command = "/help exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));

        String helpText = "Commands:  \n" +
                "/connect <userOrGroupId> - Start chat with this user/group \n" +
                "/login <username> <passowrd> - login user with username and password \n" +
                "/register <name> <username> <passowrd> - register user with given name, username and password \n" +
                "/follow <userId> - follow user with this userId \n" +
                "/unfollow <userId> - unfollow user with this userId \n" +
                "/getChats - get a list of followers this user interacted with atleast once \n" +
                "/getMessages <userId> - get a list of messages exchanged between users \n" +
                "/sendMessage <userOrGroupId> <message> - Send a text message to this user/group \n" +
                "/sendFile <userOrGroupId> <fileURL> - Send a media file to this user/group \n" +
                "/forwardMessage <receiverId> <messageId> \n" +
                "/deleteMessage <messageId> - Deletes this message \n" +
                "/deleteChat <userOrGroupId> - Delete entire conversation made with this user/group \n" +
                "/mute <userOrGroupId> - Mute notifications for messages from this user/group \n" +
                "/setProfilePicture <default or forUser (userId)> <fileURL> \n" +
                "/getProfile <userId>";

        assertEquals(helpText, this.log.toString());
    }

    @Test
    public void testRegisterImpl() {
        String username = getRandomString();
        String command = "/register Test " + username + " rootroot exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals("Registration Successful\n", this.log.toString());
    }

    @Test
    public void testRegisterFail() {
        when(httpServiceMock.sendPOST(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_BAD_REQUEST)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .request(new Request.Builder().url("http://demo").build())
                        .build());

        String username = getRandomString();
        String command = "/register Test " + username + " rootroot exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals("Registration Failed\n", this.log.toString());
    }

    @Test
    public void testLoginImpl() {
        String command = "/login testuser rootroot exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Login Successful"));
    }

    @Test
    public void testLoginFail() {
        when(httpServiceMock.sendPOST(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(null);
        String command = "/login testuser rootroot exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Login Failed"));
    }

    @Test
    public void testLoginFail2() {
        when(httpServiceMock.sendPOST(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_OK)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create("22", MediaType.parse("22")))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());
        String command = "/login testuser rootroot exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Login Failed"));
    }

    @Test
    public void testFollowImpl() {
        String command = "/login testuser rootroot /follow user2 exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Follow Successful"));
    }

    @Test
    public void testFailFollowImpl() {
        when(httpServiceMock.sendPOST(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_OK)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create("22", MediaType.parse("22")))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());
        String command = "/login testuser rootroot /follow user2 exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Follow Failed"));
    }

    @Test
    public void testUnfollowImpl() {
        String command = "/login testuser rootroot /unfollow user2 exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Unfollow Successful"));
    }

    @Test
    public void testUnfollowFail() {
        when(httpServiceMock.sendPOST(anyString(), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_OK)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create("22", MediaType.parse("22")))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());
        String command = "/login testuser rootroot /unfollow user2 exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Unfollow Failed"));
    }


    @Test
    public void testGetUserImpl() {
        String user = "{" +
                "\"id\": \"id_here\"," +
                "\"username\": \"testuser\"," +
                "\"name\": \"root\"," +
                "\"deleted\": false," +
                "\"hidden\": false," +
                "\"preferences\": []," +
                "\"followers\": []," +
                "\"following\": []," +
                "\"messages\": []," +
                "\"groups\": []" +
                "}";

        when(httpServiceMock.sendPOST(contains(AppConstants.LOGIN_ENDPOINT), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_OK)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create(user, MediaType.parse(user)))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());

        String command = "/register Test testuser rootroot /login testuser rootroot exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        UserService userService = UserServiceImpl.getInstanceNonSingleton(httpServiceMock);
        this.abstractCommand.apply(new ClientUserImpl(this.log, userService, MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals(username, userService.getUser().getUsername());
    }

    @Test
    public void testSearchUsersImpl() {
        String command = "/searchUsers " + username + " exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals("Users Found\n", this.log.toString());
    }

    @Test
    public void testSearchUsersFail() {
        when(httpServiceMock.sendGET(anyString(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_BAD_REQUEST)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create("22", MediaType.parse("22")))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());
        String command = "/searchUsers " + username + " exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals("No Users Found\n", this.log.toString());
    }

    @Test
    public void testSearchUsersFail2() {
        when(httpServiceMock.sendGET(anyString(), Matchers.any()))
                .thenReturn(null);
        String command = "/searchUsers " + username + " exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals("No Users Found\n", this.log.toString());
    }

    @Test
    public void testGetMessagesImpl() {
        ClientMessagePojo message = new ClientMessagePojo();
        message.setId(new ObjectId().toString());
        message.setSenderId(new ObjectId().toString());
        message.setReceiverId(new ObjectId().toString());
        Gson gson = new Gson();
        String messages = "[" + gson.toJson(message) + "]";

        Response postResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create(messages, MediaType.parse(messages)))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendPOST(contains(AppConstants.GET_MESSAGES_ENDPOINT), Matchers.any(), Matchers.any()))
                .thenReturn(postResponse);

        String username = getRandomString();
        String command = "/register Test " + username + " rootroot /login " + username + " rootroot /getMessages abc exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Messages Found"));
    }

    @Test
    public void testGetMessagesWithoutLogin() {
        ClientMessagePojo message = new ClientMessagePojo();
        message.setId(new ObjectId().toString());
        message.setSenderId(new ObjectId().toString());
        message.setReceiverId(new ObjectId().toString());
        Gson gson = new Gson();
        String messages = "[" + gson.toJson(message) + "]";

        Response postResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create(messages, MediaType.parse(messages)))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendPOST(contains(AppConstants.GET_MESSAGES_ENDPOINT), Matchers.any(), Matchers.any()))
                .thenReturn(postResponse);

        String username = getRandomString();
        String command = "/getMessages abc exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("You must login to fetch messages!"));
    }

    @Test
    public void testGetChatsWithoutLogin() {
        Response postResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create("[user_id]", MediaType.parse("[user_id]")))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendPOST(contains(AppConstants.GET_CHATS_ENDPOINT), Matchers.any(), Matchers.any()))
                .thenReturn(postResponse);

        String username = getRandomString();
        String command = "/getChats exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("You must login to get chats."));
    }

    @Test
    public void testGetChats() {
        Response postResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create("[]", MediaType.parse("[]")))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendGET(contains(AppConstants.GET_CHATS_ENDPOINT.replace("{userId}", "id_here")), Matchers.any()))
                .thenReturn(postResponse);

        String command = "/login abc abc /getChats exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Chats Found"));
    }


//    @Test
//    public void testSendMessageImpl() {
//        ObjectId receiverId = new ObjectId();
//        String command = "/login j4inam rootroot /sendMessage " + receiverId + " Hello2 exit";
//        this.log = new StringBuilder();
//        Readable stringCommand = new StringReader(command);
//        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
//        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
//
//        assertTrue(this.log.toString().contains("Send Message Successful"));
//    }

    @Test
    public void testDeleteMessageImpl() {
        String command = "/login testuser rootroot /deleteMessage msg123 exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertTrue(this.log.toString().contains("Delete Message Successful"));
    }

    //
//    @Test
//    public void testSendFileImpl() {
//        String command = "/sendFile abc123 img.png exit";
//        this.log = new StringBuilder();
//        Readable stringCommand = new StringReader(command);
//        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
//        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
//        assertEquals("Send File Successful\n", this.log.toString());
//    }
//
//    @Test
//    public void testForwardMessageImpl() {
//        String command = "/forwardMessage abc345 msg123 exit";
//        this.log = new StringBuilder();
//        Readable stringCommand = new StringReader(command);
//        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
//        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
//        assertEquals("Forward Message Successful\n", this.log.toString());
//    }
//
//    @Test
//    public void testMuteImpl() {
//        String command = "/mute abc123 exit";
//        this.log = new StringBuilder();
//        Readable stringCommand = new StringReader(command);
//        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
//        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
//        assertEquals("Mute Successful\n", this.log.toString());
//    }
//
    @Test
    public void testDeleteChatImpl() {
        String command = "/deleteChat abc345 exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        assertEquals("Delete Chat Successful\n", this.log.toString());
    }

    @Test
    public void testSendMessage() {
        HttpService httpMock = mock(HttpService.class);
        Session sessionMock = mock(Session.class);
        RemoteEndpoint.Basic basicMock = mock(RemoteEndpoint.Basic.class);

        when(sessionMock.getBasicRemote()).thenReturn(basicMock);

        ObjectId messageId = new ObjectId();
        UserService userService = new UserServiceImpl(httpMock, sessionMock);
        Message message = Message.messageBuilder()
                .setId(messageId)
                .build();

        assertTrue(userService.sendMessage(new ObjectId().toString(), message));
        ArgumentCaptor<ServerCommand> argumentCaptor = ArgumentCaptor.forClass(ServerCommand.class);
        try {
            verify(basicMock).sendObject(argumentCaptor.capture());
        } catch (IOException e) {
            fail("IOException caused");
        } catch (EncodeException e) {
            fail("EncodeException caused");
        }
        assertEquals(argumentCaptor.getValue().getMessage().getId(), messageId);
    }

    @Test
    public void testSendMessageNoSession() {
        HttpService httpMock = mock(HttpService.class);
        ObjectId messageId = new ObjectId();
        UserService userService = new UserServiceImpl(httpMock, null);
        Message message = Message.messageBuilder()
                .setId(messageId)
                .build();

        assertFalse(userService.sendMessage(new ObjectId().toString(), message));
    }

    @Test
    public void testSendMessageThrowingException() {
        HttpService httpMock = mock(HttpService.class);
        Session sessionMock = mock(Session.class);

        when(sessionMock.getBasicRemote()).thenThrow(IOException.class);

        ObjectId messageId = new ObjectId();
        UserService userService = new UserServiceImpl(httpMock, sessionMock);
        Message message = Message.messageBuilder()
                .setId(messageId)
                .build();

        assertFalse(userService.sendMessage(new ObjectId().toString(), message));
    }

    @Test
    public void testSendFileFail() {
        HttpService httpMock = mock(HttpService.class);
        Session sessionMock = mock(Session.class);
        RemoteEndpoint.Basic basicMock = mock(RemoteEndpoint.Basic.class);

        when(sessionMock.getBasicRemote()).thenReturn(basicMock);

        ObjectId messageId = new ObjectId();
        UserService userService = new UserServiceImpl(httpMock, sessionMock);
        Message message = Message.messageBuilder()
                .setId(messageId)
                .build();

        assertFalse(userService.sendFile(new ObjectId().toString(), "abc"));
    }

    @Test
    public void testSendFile() {
        HttpService httpMock = mock(HttpService.class);
        Session sessionMock = mock(Session.class);
        RemoteEndpoint.Basic basicMock = mock(RemoteEndpoint.Basic.class);

        ObjectId messageId = new ObjectId();
        ObjectId senderId = new ObjectId();
        ObjectId receiverId = new ObjectId();
        String url = "http://react.png";

        ClientUserPojo user = new ClientUserPojo();
        user.setId(senderId.toString());

        ClientMessagePojo messagePojo = new ClientMessagePojo();
        messagePojo.setReceiverId(receiverId.toString());
        messagePojo.setSenderId(senderId.toString());
        messagePojo.setId(messageId.toString());
        messagePojo.setEncryptionLevel(EncrpytionLevel.BASIC);
        messagePojo.setContent(url);
        messagePojo.setDeleted(false);
        messagePojo.setHidden(false);
        messagePojo.setForwarded(false);
        messagePojo.setTimestamp(new Date());
        messagePojo.setExpiryDate(new Date());
        messagePojo.setMessageType(MessageType.MEDIA);
        messagePojo.setTags(new ArrayList<>());

        Gson gson = new Gson();

        when(httpMock.sendPOST(contains(AppConstants.CREATE_MEDIA_MESSAGE_ENDPOINT), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_OK)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create(gson.toJson(messagePojo), MediaType.parse(gson.toJson(messagePojo))))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());

        when(sessionMock.getBasicRemote()).thenReturn(basicMock);

        UserService userService = new UserServiceImpl(httpMock, sessionMock, user);

        String filePath = "src/main/resources/react.png";
        assertTrue(userService.sendFile(receiverId.toString(), filePath));
        ArgumentCaptor<ServerCommand> argumentCaptor = ArgumentCaptor.forClass(ServerCommand.class);
        try {
            verify(basicMock).sendObject(argumentCaptor.capture());
            assertEquals(messageId, argumentCaptor.getValue().getMessage().getId());
            assertEquals(url, argumentCaptor.getValue().getMessage().getContent());
            assertEquals(ServerCommandType.SEND_MESSAGE, argumentCaptor.getValue().getType());
        } catch (IOException e) {
            fail("IOException caused");
        } catch (EncodeException e) {
            fail("EncodeException caused");
        }

    }

    @Test
    public void testSendFileFailNoSession() {
        HttpService httpMock = mock(HttpService.class);
        Session sessionMock = mock(Session.class);
        RemoteEndpoint.Basic basicMock = mock(RemoteEndpoint.Basic.class);

        ObjectId messageId = new ObjectId();
        ObjectId senderId = new ObjectId();
        ObjectId receiverId = new ObjectId();
        String url = "http://react.png";

        ClientUserPojo user = new ClientUserPojo();
        user.setId(senderId.toString());

        ClientMessagePojo messagePojo = new ClientMessagePojo();
        messagePojo.setReceiverId(receiverId.toString());
        messagePojo.setSenderId(senderId.toString());
        messagePojo.setId(messageId.toString());
        messagePojo.setEncryptionLevel(EncrpytionLevel.BASIC);
        messagePojo.setContent(url);
        messagePojo.setDeleted(false);
        messagePojo.setHidden(false);
        messagePojo.setForwarded(false);
        messagePojo.setTimestamp(new Date());
        messagePojo.setExpiryDate(new Date());
        messagePojo.setMessageType(MessageType.MEDIA);
        messagePojo.setTags(new ArrayList<>());

        Gson gson = new Gson();

        when(httpMock.sendPOST(contains(AppConstants.CREATE_MEDIA_MESSAGE_ENDPOINT), Matchers.any(), Matchers.any()))
                .thenReturn(new Response.Builder().code(HttpStatus.SC_OK)
                        .protocol(Protocol.HTTP_1_1)
                        .message("no return message")
                        .body(ResponseBody.create(gson.toJson(messagePojo), MediaType.parse(gson.toJson(messagePojo))))
                        .request(new Request.Builder().url("http://demo").build())
                        .build());

        when(sessionMock.getBasicRemote()).thenReturn(basicMock);

        UserService userService = new UserServiceImpl(httpMock, null, user);

        String filePath = "src/main/resources/react.png";
        assertFalse(userService.sendFile(receiverId.toString(), filePath));
    }

    @Test
    public void getGetProfile() {
        String pojo = "{id='5e7786c6424fbe6ad25f16a1', username='abc', name='abc'}";
        Response postResponse = new Response.Builder().code(HttpStatus.SC_OK)
                .protocol(Protocol.HTTP_1_1)
                .message("no return message")
                .body(ResponseBody.create(pojo, MediaType.parse(pojo)))
                .request(new Request.Builder().url("http://demo").build())
                .build();

        when(httpServiceMock.sendGET(contains(AppConstants.GET_PROFILE_ENDPOINT.replace("{id}", "5e7786c6424fbe6ad25f16a1")), Matchers.any()))
                .thenReturn(postResponse);

        String command = "/login abc abc /getProfile 5e7786c6424fbe6ad25f16a1" + " exit";
        this.log = new StringBuilder();
        Readable stringCommand = new StringReader(command);
        this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
        this.abstractCommand.apply(new ClientUserImpl(this.log, UserServiceImpl.getInstanceNonSingleton(httpServiceMock), MessageServiceImpl.getInstanceNonSingleton(httpServiceMock)));
        System.out.println(this.log.toString());
        assertTrue(this.log.toString().contains("User's Profile"));
    }

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
}
