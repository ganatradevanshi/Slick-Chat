package fse.team2.slickclient.websockets;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.websockets.commands.ClientCommand;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.CloseReason;
import javax.websocket.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class MainClientEndpointTests {

    private MainClientEndpoint endpoint;
    private Session session;

    @Before
    public void setUp() {
        endpoint = new MainClientEndpoint();
        session = mock(Session.class);
        endpoint.onOpen(session);
    }

    @Test
    public void onMessageTest() {
        Message message = Message.messageBuilder()
                .setSenderId(new ObjectId())
                .setId(new ObjectId())
                .setReceiverId(new ObjectId())
                .setMessageContent("Hello World")
                .build();

        ClientCommand command = ClientCommand.getCommandBuilder()
                .setCommandType(ClientCommandType.RECEIVE_MESSAGE)
                .setMessage(message)
                .build();

        endpoint.onMessage(session, command);
    }

    @After
    public void cleanUp() {
        endpoint.onClose(session, new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Closed from test"));
    }
}
