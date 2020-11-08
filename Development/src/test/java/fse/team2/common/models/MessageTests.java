package fse.team2.common.models;

import fse.team2.common.models.mongomodels.Message;

import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

public class MessageTests {
    Message message;
    String sender;
    String receiver;
    String messageContent;

    @Before
    public void setUp() {
        sender = "messageSender";
        receiver = "messageReceiver";
        messageContent = "Hey!, How are you?";
    }

    @Test
    public void createMessage() {
        ObjectId id = new ObjectId();
        Date timestamp = new Date();
        message = Message.messageBuilder()
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
                .setGroupMessage(false)
                .setThreadHead(id)
                .build();

        assertEquals(id, message.getId());
        assertEquals(id, message.getSenderId());
        assertEquals(id, message.getReceiverId());
        assertEquals(0, message.getTags().size());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(timestamp, message.getExpiryDate());
        assertEquals(EncrpytionLevel.BASIC, message.getEncryptionLevel());
        assertEquals(MessageType.TEXT, message.getMessageType());
        assertFalse(message.isForwarded());
        assertFalse(message.isGroupMessage());
        assertEquals(id, message.getThreadHead());
        assertFalse(message.isThread());
    }

    @Test
    public void createMessage2() {
        ObjectId id = new ObjectId();
        Date timestamp = new Date();
        message = Message.messageBuilder()
                .setId(id)
                .setDeleted(false)
                .setHidden(true)
                .setForwarded(false)
                .setEncryptionLevel(EncrpytionLevel.NONE)
                .setMessageType(MessageType.TEXT)
                .setExpiryDate(timestamp)
                .setReceiverId(id)
                .setSenderId(id)
                .setTags(new ArrayList<>())
                .setTimestamp(timestamp)
                .setGroupMessage(false)
                .build();

        assertEquals(id, message.getId());
        assertEquals(id, message.getSenderId());
        assertEquals(id, message.getReceiverId());
        assertEquals(0, message.getTags().size());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(timestamp, message.getExpiryDate());
        assertEquals(EncrpytionLevel.NONE, message.getEncryptionLevel());
        assertEquals(MessageType.TEXT, message.getMessageType());
        assertFalse(message.isForwarded());
        assertFalse(message.isGroupMessage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheckMessage1() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setId(null)
                .build();
    }

    @Test
    public void nullCheckMessage2() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setExpiryDate(null)
                .build();
        assertNotNull(message.getExpiryDate());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheckMessage3() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setSenderId(null)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheckMessage4() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setReceiverId(null)
                .build();
    }

    @Test
    public void nullCheckMessage5() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setTags(null)
                .build();
        assertEquals(Collections.EMPTY_LIST, message.getTags());
    }

    @Test
    public void nullCheckMessage6() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setTimestamp(null)
                .build();
        assertNotNull(message.getTimestamp());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheckMessage7() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setMessageType(null)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheckMessage8() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setEncryptionLevel(null)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCheckMessage9() {
        ObjectId id = new ObjectId();
        message = Message.messageBuilder()
                .setMessageContent(null)
                .build();
    }
}
