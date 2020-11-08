package com.neu.prattle.websockets.endpoints;

import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.GroupService;
import com.neu.prattle.service.dbservice.MessageService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.websocket.endpoints.MainEndpoint;
import fse.team2.common.models.mongomodels.Group;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.websockets.commands.ClientCommand;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
import fse.team2.common.websockets.commands.ServerCommand;
import fse.team2.common.websockets.commands.enums.ServerCommandType;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class MainEndpointTests {

    private MainEndpoint endpoint;
    private DatabaseService<UserModel> userService;
    private Session session;
    private RemoteEndpoint.Basic basic;

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
    public void setUp() {
        userService = UserService.getInstance();
        endpoint = new MainEndpoint();
        session = mock(Session.class);
        basic = mock(RemoteEndpoint.Basic.class);
    }

    @Test
    public void testUserDoesnotExists() {
        ObjectId id = new ObjectId();
        String username = "qwerty" + getRandomString();
        UserModel user = UserModel.userBuilder()
                .setUsername(username)
                .setId(id)
                .build();

        ArgumentCaptor<CloseReason> argumentCaptor = ArgumentCaptor.forClass(CloseReason.class);
        endpoint.onOpen(session, username);

        try {
            verify(session).close(argumentCaptor.capture());
            assertEquals(CloseReason.CloseCodes.CANNOT_ACCEPT, argumentCaptor.getValue().getCloseCode());
        } catch (IOException e) {
            fail("IOException thrown in test case");
        }
    }

    @Test
    public void testUserExists() {
        ObjectId id = new ObjectId();
        String username = "qwerty1" + getRandomString();
        UserModel user = UserModel.userBuilder()
                .setUsername(username)
                .setId(id)
                .build();

        userService.add(user);

        ArgumentCaptor<CloseReason> argumentCaptor = ArgumentCaptor.forClass(CloseReason.class);
        endpoint.onOpen(session, username);

        try {
            verify(session, times(0)).close(argumentCaptor.capture());
        } catch (IOException e) {
            fail("IOException thrown in test case");
        } finally {
            userService.deleteById(id);
        }
    }

    @Test
    public void testOnMessageSendMessage() {
        ObjectId id = new ObjectId();
        String username = "qwerty" + getRandomString();
        UserModel user = UserModel.userBuilder()
                .setUsername(username)
                .setId(id)
                .build();

        userService.add(user);

        endpoint.onOpen(session, username);

        Message message = Message.messageBuilder()
                .setSenderId(id)
                .setId(new ObjectId())
                .setReceiverId(id)
                .setMessageType(MessageType.TEXT)
                .setMessageContent("Hello World")
                .build();

        ServerCommand command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(message)
                .build();

        ArgumentCaptor<ClientCommand> argumentCaptor = ArgumentCaptor.forClass(ClientCommand.class);
        when(session.getBasicRemote()).thenReturn(basic);

        endpoint.onMessage(session, command);
        try {
            verify(basic, times(2)).sendObject(argumentCaptor.capture());
        } catch (IOException e) {
            fail("IOException caused");
        } catch (EncodeException e) {
            fail("Encode caused");
        }

        // Check if messaged was entered to db.
        DatabaseService<Message> messageDatabaseService = MessageService.getInstance();
        Message messageFromDb = messageDatabaseService.findById(message.getId());

        // Cleanup
        userService.deleteById(id);
        messageDatabaseService.deleteById(messageFromDb.getId());

        ClientCommand received = argumentCaptor.getValue();
        assertEquals(ClientCommandType.SEND_MESSAGE_SUCCESS, received.getType());
        assertEquals(message.getId(), received.getMessage().getId());
        assertEquals(message.getContent(), received.getMessage().getContent());
    }

    @Test
    public void testOnMessageSendMessage2() {
        ObjectId id1 = new ObjectId();
        String username1 = "qwerty" + getRandomString();
        UserModel user1 = UserModel.userBuilder()
                .setUsername(username1)
                .setId(id1)
                .build();

        ObjectId id2 = new ObjectId();
        String username2 = "abcdef" + getRandomString();
        UserModel user2 = UserModel.userBuilder()
                .setUsername(username2)
                .setId(id2)
                .build();

        userService.add(user1);
        userService.add(user2);

        // Two different sessions
        Session session1 = mock(Session.class);
        Session session2 = mock(Session.class);

        // Two users connect to the websocket endpoint
        endpoint.onOpen(session1, username1);
        endpoint.onOpen(session2, username2);

        // Message to be sent from user1 to user2
        Message message = Message.messageBuilder()
                .setSenderId(id1)
                .setId(new ObjectId())
                .setReceiverId(id2)
                .setMessageType(MessageType.TEXT)
                .setMessageContent("Hello World")
                .build();

        ServerCommand command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(message)
                .build();

        ArgumentCaptor<ClientCommand> argumentCaptor = ArgumentCaptor.forClass(ClientCommand.class);
        when(session2.getBasicRemote()).thenReturn(basic);
        when(session1.getBasicRemote()).thenReturn(mock(RemoteEndpoint.Basic.class));

        endpoint.onMessage(session1, command);
        try {
            verify(basic, times(1)).sendObject(argumentCaptor.capture());
        } catch (IOException e) {
            fail("IOException caused");
        } catch (EncodeException e) {
            fail("Encode caused");
        }

        // Check if messaged was entered to db.
        DatabaseService<Message> messageDatabaseService = MessageService.getInstance();
        Message messageFromDb = messageDatabaseService.findById(message.getId());

        // Cleanup
        userService.deleteById(id1);
        userService.deleteById(id2);
        messageDatabaseService.deleteById(messageFromDb.getId());

        ClientCommand received = argumentCaptor.getValue();
        assertEquals(ClientCommandType.RECEIVE_MESSAGE, received.getType());
        assertEquals(message.getId(), received.getMessage().getId());
        assertEquals(message.getContent(), received.getMessage().getContent());
        assertEquals(message.getSenderId(), received.getMessage().getSenderId());
        assertEquals(message.getReceiverId(), received.getMessage().getReceiverId());
    }

    @Test
    public void testGroupMessage() {
        DatabaseService<Group> groupService = GroupService.getInstance();
        DatabaseService<UserModel> userService = UserService.getInstance();
        String groupName = "Demo Group" + getRandomString();

        UserModel user = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();
        UserModel user2 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();
        UserModel user3 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();
        Group group = Group.groupBuilder()
                .setId(new ObjectId())
                .setUsers(Collections.emptyList())
                .setCurrentInvitedUsers(Collections.emptyList())
                .build();
        UserModel moderatorUser = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Moderator User" + getRandomString())
                .build();

        //add user and mocerator to DB
        userService.add(moderatorUser);
        userService.add(user);
        userService.add(user2);
        userService.add(user3);

        // add moderator to a list
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());
        users.add(user2.getId());
        users.add(user3.getId());
        // set group name and moderators in the group and add it to DB
        group.setName(groupName);
        group.setModerators(moderators);
        group.setUsers(users);
        groupService.add(group);

        Session sessionUser = mock(Session.class);
        Session sessionUser2 = mock(Session.class);
        Session sessionUser3 = mock(Session.class);
        Session sessionMod = mock(Session.class);
        RemoteEndpoint.Basic basicUser = mock(RemoteEndpoint.Basic.class);
        RemoteEndpoint.Basic basicUser2 = mock(RemoteEndpoint.Basic.class);
        RemoteEndpoint.Basic basicUser3 = mock(RemoteEndpoint.Basic.class);
        RemoteEndpoint.Basic basicMod = mock(RemoteEndpoint.Basic.class);

        when(sessionUser.getBasicRemote()).thenReturn(basicUser);
        when(sessionUser2.getBasicRemote()).thenReturn(basicUser2);
        when(sessionUser3.getBasicRemote()).thenReturn(basicUser3);
        when(sessionMod.getBasicRemote()).thenReturn(basicMod);

        endpoint.onOpen(sessionUser, user.getUsername());
        endpoint.onOpen(sessionUser2, user2.getUsername());
        endpoint.onOpen(sessionUser3, user3.getUsername());
        endpoint.onOpen(sessionMod, moderatorUser.getUsername());

        Message message = Message.messageBuilder()
                .setSenderId(user.getId())
                .setId(new ObjectId())
                .setReceiverId(group.getId())
                .setGroupMessage(true)
                .setMessageContent("Hello World")
                .setMessageType(MessageType.TEXT)
                .build();

        ServerCommand command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(message)
                .build();

        endpoint.onMessage(sessionUser, command);

        userService.deleteById(user.getId());
        userService.deleteById(user2.getId());
        userService.deleteById(user3.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());

        ArgumentCaptor<ClientCommand> ac1 = ArgumentCaptor.forClass(ClientCommand.class);
        ArgumentCaptor<ClientCommand> ac2 = ArgumentCaptor.forClass(ClientCommand.class);
        ArgumentCaptor<ClientCommand> ac3 = ArgumentCaptor.forClass(ClientCommand.class);
        ArgumentCaptor<ClientCommand> ac4 = ArgumentCaptor.forClass(ClientCommand.class);
        try {
            verify(basicUser, times(1)).sendObject(ac1.capture());
            verify(basicUser2, times(1)).sendObject(ac2.capture());
            verify(basicUser3, times(1)).sendObject(ac3.capture());
            assertEquals(ClientCommandType.SEND_MESSAGE_SUCCESS, ac1.getValue().getType());
            assertEquals(message.getContent(), ac2.getValue().getMessage().getContent());
            assertEquals(message.getContent(), ac3.getValue().getMessage().getContent());
        } catch (Exception e) {
            fail("Exception thrown");
        }

    }

    @Test
    public void testReceiverNotOnline() {
        ObjectId id1 = new ObjectId();
        String username1 = "qwerty" + getRandomString();
        UserModel user1 = UserModel.userBuilder()
                .setUsername(username1)
                .setId(id1)
                .build();

        ObjectId id2 = new ObjectId();
        String username2 = "abcdef" + getRandomString();
        UserModel user2 = UserModel.userBuilder()
                .setUsername(username2)
                .setId(id2)
                .build();

        userService.add(user1);
        userService.add(user2);

        // Two different sessions
        Session session1 = mock(Session.class);

        // Two users connect to the websocket endpoint
        endpoint.onOpen(session1, username1);

        // Message to be sent from user1 to user2
        ObjectId messageId = new ObjectId();
        Message message = Message.messageBuilder()
                .setSenderId(id1)
                .setId(messageId)
                .setReceiverId(id2)
                .setMessageContent("Hello World")
                .setMessageType(MessageType.TEXT)
                .build();

        ServerCommand command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(message)
                .build();

        when(session1.getBasicRemote()).thenReturn(basic);
        endpoint.onMessage(session1, command);

        // Check if messaged was entered to db.
        DatabaseService<Message> messageDatabaseService = MessageService.getInstance();
        Message messageFromDb = messageDatabaseService.findById(messageId);

        // Cleanup
        userService.deleteById(id1);
        userService.deleteById(id2);
        messageDatabaseService.deleteById(messageId);
        messageDatabaseService.deleteById(messageFromDb.getId());

        assertEquals(messageId, messageFromDb.getId());
        assertEquals(message.getReceiverId(), messageFromDb.getReceiverId());
        assertEquals(message.getSenderId(), messageFromDb.getSenderId());
    }

    @After
    public void cleanUp() {
        endpoint.onClose(session);
    }


}
