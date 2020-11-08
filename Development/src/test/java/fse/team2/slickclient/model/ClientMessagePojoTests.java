package fse.team2.slickclient.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientMessagePojoTests {
  private ClientMessagePojo clientMessagePojo;

  @Before
  public void setUp() {
    this.clientMessagePojo = new ClientMessagePojo();
  }

  @Test
  public void testId() {
    this.clientMessagePojo.setId("123");
    assertEquals("123", this.clientMessagePojo.getId());
  }

  @Test
  public void testSenderId() {
    this.clientMessagePojo.setSenderId("123");
    assertEquals("123", this.clientMessagePojo.getSenderId());
  }

  @Test
  public void testReceiverId() {
    this.clientMessagePojo.setReceiverId("123");
    assertEquals("123", this.clientMessagePojo.getReceiverId());
  }

  @Test
  public void testTimestamp() {
    Date date = new Date();
    this.clientMessagePojo.setTimestamp(date);
    assertEquals(date, this.clientMessagePojo.getTimestamp());
  }

  @Test
  public void testMessageType() {
    this.clientMessagePojo.setMessageType(MessageType.TEXT);
    assertEquals(MessageType.TEXT, this.clientMessagePojo.getMessageType());
  }

  @Test
  public void testDeleted() {
    this.clientMessagePojo.setDeleted(true);
    assertTrue(this.clientMessagePojo.isDeleted());
  }

  @Test
  public void testHidden() {
    this.clientMessagePojo.setHidden(true);
    assertTrue(this.clientMessagePojo.isHidden());
  }

  @Test
  public void testisForwarded() {
    this.clientMessagePojo.setForwarded(true);
    assertTrue(this.clientMessagePojo.isForwarded());
  }

  @Test
  public void testExpiryDate() {
    Date date = new Date();
    this.clientMessagePojo.setExpiryDate(date);
    assertEquals(date, this.clientMessagePojo.getExpiryDate());
  }

  @Test
  public void testTags() {
    List<String> tags = new ArrayList<>();
    this.clientMessagePojo.setTags(tags);
    assertEquals(tags, this.clientMessagePojo.getTags());
  }

  @Test
  public void testEncryptionLevel() {
    this.clientMessagePojo.setEncryptionLevel(EncrpytionLevel.BASIC);
    assertEquals(EncrpytionLevel.BASIC, this.clientMessagePojo.getEncryptionLevel());
  }

  @Test
  public void testContent() {
    this.clientMessagePojo.setContent("Hello!");
    assertEquals("Hello!", this.clientMessagePojo.getContent());
  }
}
