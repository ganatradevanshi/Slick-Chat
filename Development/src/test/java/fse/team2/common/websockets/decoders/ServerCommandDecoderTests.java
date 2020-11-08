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
import fse.team2.common.websockets.commands.ServerCommand;
import fse.team2.common.websockets.commands.enums.ServerCommandType;
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
import static org.junit.Assert.fail;

public class ServerCommandDecoderTests {
    private Decoder.Text<ServerCommand> decoder;
    private Message sampleMessage;

    private String commandToJson(ServerCommand command) {
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
        }).create().toJson(command, ServerCommand.class);
    }

    @Before
    public void setUp() {
        decoder = new ServerCommandDecoder();
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
    public void decodeTest1() {
        ServerCommandType type = ServerCommandType.SEND_MESSAGE;
        ServerCommand command = ServerCommand.getCommandBuilder()
                .setMessage(sampleMessage)
                .setCommandType(type)
                .build();

        String json = commandToJson(command);
        try {
            ServerCommand decodedCommand = decoder.decode(json);
            assertEquals(type, decodedCommand.getType());
            assertEquals(sampleMessage.getId(), decodedCommand.getMessage().getId());
        } catch (DecodeException e) {
            fail("Decode exception occured");
        }
    }

    @Test
    public void decodeTest2() {
        ServerCommandType type = ServerCommandType.FORWARD_MESSAGE;
        ServerCommand command = ServerCommand.getCommandBuilder()
                .setMessage(sampleMessage)
                .setCommandType(type)
                .build();

        String json = commandToJson(command);
        try {
            ServerCommand decodedCommand = decoder.decode(json);
            assertEquals(type, decodedCommand.getType());
            assertEquals(sampleMessage.getId(), decodedCommand.getMessage().getId());
        } catch (DecodeException e) {
            fail("Decode exception occured");
        }
    }

    @Test
    public void decodeFailTest() {
        ServerCommandType type = ServerCommandType.SEND_MESSAGE;
        ServerCommand command = ServerCommand.getCommandBuilder()
                .setMessage(sampleMessage)
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .build();

        String json = "{}sa";
        try {
            ServerCommand decodedCommand = decoder.decode(json);
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
