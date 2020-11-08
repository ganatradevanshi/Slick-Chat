package fse.team2.common.websockets.encoders;

import com.google.gson.GsonBuilder;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;
import fse.team2.common.websockets.commands.ClientCommand;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
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

public class ClientCommandEncoderTests {

    private Encoder.Text<ClientCommand> encoder;
    private Message sampleMessage;
    private ClientCommand command1;

    private String commandToJson(ClientCommand command) {
        return new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create().toJson(command, ClientCommand.class);
    }

    @Before
    public void setUp() {
        encoder = new ClientCommandEncoder();
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
        ClientCommandType type = ClientCommandType.RECEIVE_MESSAGE;
        ClientCommand command = ClientCommand.getCommandBuilder()
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
