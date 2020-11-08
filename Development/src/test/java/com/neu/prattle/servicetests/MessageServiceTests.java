package com.neu.prattle.servicetests;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.MessageService;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MessageServiceTests {

  DatabaseService<Message> service;

  @Before
  public void setUp() {
    service = MessageService.getInstance();

  }

  @Test
  public void addMessageAndDelete() {
    ObjectId id = new ObjectId();
    String content = "Hello world!";
    Date timestamp = new Date();
    Message message = Message.messageBuilder()
        .setId(id)
        .setSenderId(id)
        .setReceiverId(id)
        .setTimestamp(timestamp)
        .setExpiryDate(timestamp)
        .setEncryptionLevel(EncrpytionLevel.BASIC)
        .setMessageType(MessageType.TEXT)
        .setDeleted(false)
        .setHidden(false)
        .setMessageContent(content)
        .setTags(new ArrayList<>())
        .build();

    service.add(message);
    Message foundMessage = service.findById(id);
    assertEquals(id, foundMessage.getId());
    assertEquals(id, foundMessage.getSenderId());
    assertEquals(id, foundMessage.getReceiverId());
    assertEquals(0, foundMessage.getTags().size());
    assertFalse(foundMessage.isDeleted());
    assertFalse(foundMessage.isHidden());
    assertEquals(timestamp, foundMessage.getTimestamp());
    assertEquals(timestamp, foundMessage.getExpiryDate());
    assertEquals(EncrpytionLevel.BASIC, foundMessage.getEncryptionLevel());
    assertEquals(MessageType.TEXT, foundMessage.getMessageType());
    service.deleteById(id);
  }

  @Test
  public void findBy() {
    ObjectId id = new ObjectId();
    String content = "Hello world!";
    Date timestamp = new Date();
    Message message = Message.messageBuilder()
        .setId(id)
        .setSenderId(id)
        .setReceiverId(id)
        .setTimestamp(timestamp)
        .setExpiryDate(timestamp)
        .setEncryptionLevel(EncrpytionLevel.BASIC)
        .setMessageType(MessageType.TEXT)
        .setDeleted(false)
        .setHidden(false)
        .setMessageContent(content)
        .setTags(new ArrayList<>())
        .build();

    service.add(message);
    Message foundMessage = service.findBy(Filters.eq("_id", id)).next();
    assertEquals(id, foundMessage.getId());
    assertEquals(id, foundMessage.getSenderId());
    assertEquals(id, foundMessage.getReceiverId());
    assertEquals(0, foundMessage.getTags().size());
    assertFalse(foundMessage.isDeleted());
    assertFalse(foundMessage.isHidden());
    assertEquals(timestamp, foundMessage.getTimestamp());
    assertEquals(timestamp, foundMessage.getExpiryDate());
    assertEquals(EncrpytionLevel.BASIC, foundMessage.getEncryptionLevel());
    assertEquals(MessageType.TEXT, foundMessage.getMessageType());
    assertEquals(content, foundMessage.getContent());
    service.deleteById(id);
  }

  @Test
  public void replaceOne() {
    ObjectId id = new ObjectId();
    String content = "Hello world!";
    String anotherContent = "Hi there!";
    Date timestamp = new Date();
    Message message = Message.messageBuilder()
        .setId(id)
        .setSenderId(id)
        .setReceiverId(id)
        .setTimestamp(timestamp)
        .setExpiryDate(timestamp)
        .setEncryptionLevel(EncrpytionLevel.BASIC)
        .setMessageType(MessageType.TEXT)
        .setDeleted(false)
        .setHidden(false)
        .setMessageContent(content)
        .setTags(new ArrayList<>())
        .build();

    Message anotherMessage = Message.messageBuilder()
        .setId(id)
        .setSenderId(id)
        .setReceiverId(id)
        .setTimestamp(timestamp)
        .setExpiryDate(timestamp)
        .setEncryptionLevel(EncrpytionLevel.BASIC)
        .setMessageType(MessageType.TEXT)
        .setDeleted(false)
        .setHidden(false)
        .setMessageContent(anotherContent)
        .setTags(new ArrayList<>())
        .build();

    service.add(message);
    service.replaceOne(Filters.eq("_id", id), anotherMessage);
    Message foundMessage = service.findById(id);
    assertEquals(anotherContent, foundMessage.getContent());
    service.deleteById(id);
  }

  @Test
  public void updateOne() {
    ObjectId id = new ObjectId();
    String content = "Good Day!";
    Date timestamp = new Date();
    Message message = Message.messageBuilder()
        .setId(id)
        .setSenderId(id)
        .setReceiverId(id)
        .setTimestamp(timestamp)
        .setExpiryDate(timestamp)
        .setEncryptionLevel(EncrpytionLevel.BASIC)
        .setMessageType(MessageType.TEXT)
        .setDeleted(false)
        .setHidden(false)
        .setMessageContent(content)
        .setTags(new ArrayList<>())
        .build();

    BasicDBObject updateQuery = new BasicDBObject();
    updateQuery.append("_id", id);

    service.add(message);
    service.updateOne(updateQuery, new BasicDBObject("$set", new BasicDBObject("deleted", true)));
    Message foundMessage = service.findById(id);
    assertEquals(true, foundMessage.isDeleted());
    service.deleteById(id);
  }

//    Need to modify this test, can't assume that that there would be messages stored in the database
  
//    @Test
//    public void findAll() {
//
//        String contentOne = "Hello world!";
//        String contentTwo = "Forward this Message!";
//        String contentThree = "Hello world!";
//
//        Iterator<Message> it = service.findAll();
//
//            assertEquals(contentOne, it.next().getContent());
//            Message nextMsg = it.next();
//            assertEquals(contentTwo, nextMsg.getContent());
//
//    }


}
