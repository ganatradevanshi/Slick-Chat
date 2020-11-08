package fse.team2.common.websockets.decoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.websockets.commands.ClientCommand;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class ClientCommandDecoderTests {
    private Decoder.Text<ClientCommand> decoder;
    private Message sampleMessage;

    private String commandToJson(ClientCommand command) {
        return new GsonBuilder().registerTypeAdapter(ObjectId.class, new JsonSerializer<ObjectId>() {
            @Override
            public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.toHexString());
            }
        }).registerTypeAdapter(ObjectId.class, new JsonDeserializer<ObjectId>() {
            @Override
            public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new ObjectId(json.getAsString());
            }
        }).create().toJson(command, ClientCommand.class);
    }

    @Before
    public void setUp() {
        decoder = new ClientCommandDecoder();
        decoder.init(null);

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
    public void testWillDecode() {
        assertTrue(decoder.willDecode("{}"));
        assertFalse(decoder.willDecode(null));
    }

    @Test
    public void decodeTest() {
        ClientCommandType type = ClientCommandType.RECEIVE_MESSAGE;
        ClientCommand command = ClientCommand.getCommandBuilder()
                .setMessage(sampleMessage)
                .setCommandType(ClientCommandType.RECEIVE_MESSAGE)
                .build();

        String json = commandToJson(command);
        try {
            ClientCommand decodedCommand = decoder.decode(json);
            assertEquals(type, decodedCommand.getType());
            assertEquals(sampleMessage.getId(), decodedCommand.getMessage().getId());
        } catch (DecodeException e) {
            fail("Decode exception occured");
        }
    }

    @Test
    public void decodeFailTest() {
        ClientCommandType type = ClientCommandType.RECEIVE_MESSAGE;
        ClientCommand command = ClientCommand.getCommandBuilder()
                .setMessage(sampleMessage)
                .setCommandType(ClientCommandType.RECEIVE_MESSAGE)
                .build();

        String json = "{}sa";
        try {
            ClientCommand decodedCommand = decoder.decode(json);
            fail("Exception should have been thrown");
        } catch (DecodeException e) {
            assertTrue(decoder.willDecode(""));
        }
    }

    @After
    public void cleanUp() {
        decoder.destroy();
    }

}
