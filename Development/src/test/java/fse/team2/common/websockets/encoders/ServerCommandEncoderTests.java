package fse.team2.common.websockets.encoders;

import com.google.gson.GsonBuilder;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.websockets.commands.ServerCommand;
import fse.team2.common.websockets.commands.enums.ServerCommandType;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ServerCommandEncoderTests {

    private Encoder.Text<ServerCommand> encoder;
    private Message sampleMessage;
    private ServerCommand command1;

    private String commandToJson(ServerCommand command) {
        return new GsonBuilder().create().toJson(command, ServerCommand.class);
    }

    @Before
    public void setUp() {
        encoder = new ServerCommandEncoder();
        encoder.init(null);

        ObjectId id = new ObjectId();
        Date timestamp = new Date();
        sampleMessage = Message.messageBuilder()
                .setId(id)
                .setDeleted(false)
                .setHidden(true)
                .setForwarded(false)
                .setEncryptionLevel(EncrpytionLevel.BASIC)
                .setMessageType(MessageType.TEXT)
                .setExpiryDate(timestamp)
                .setReceiverId(id)
                .setSenderId(id)
                .setTags(new ArrayList<>())
                .setTimestamp(timestamp)
                .build();
    }

    @Test
    public void testEncode() {
        ServerCommandType type = ServerCommandType.SEND_MESSAGE;
        ServerCommand command = ServerCommand.getCommandBuilder()
                .setMessage(sampleMessage)
                .setCommandType(type)
                .build();

        String json = commandToJson(command);
        try {
            assertEquals(json, encoder.encode(command));
        } catch (EncodeException e) {
            fail("EncodException was thrown");
        }
    }

    @After
    public void cleanUp() {
        encoder.destroy();
    }
}
