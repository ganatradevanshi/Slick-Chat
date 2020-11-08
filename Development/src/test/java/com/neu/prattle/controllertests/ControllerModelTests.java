package com.neu.prattle.controllertests;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import fse.team2.common.models.controllermodels.SenderReceiverParams;
import fse.team2.common.models.controllermodels.UserCredentialsParams;
import fse.team2.common.models.controllermodels.UserInformation;
import fse.team2.common.models.controllermodels.UserMessageParams;
import fse.team2.common.models.controllermodels.UserMessagesInformation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControllerModelTests {
  private UserMessagesInformation userMessagesInformation;
  private UserMessageParams userMessageParams;
  private UserInformation userInformation;
  private UserCredentialsParams userCredentialsParams;
  private SenderReceiverParams senderReceiverParams;

  @Before
  public void setUp() {
    userMessagesInformation = new UserMessagesInformation();
    userMessageParams = new UserMessageParams();
    userInformation = new UserInformation();
    userCredentialsParams = new UserCredentialsParams();
    senderReceiverParams = new SenderReceiverParams();
  }

  @Test
  public void userMessageInformationTest() {
    String username = "UserName";
    String sender = "Sender";
    String receiver = "Receiver";
    String group = "Group";
    String message = "Message";
    userMessagesInformation.setUsername(username);
    userMessagesInformation.setGroup(group);
    userMessagesInformation.setSender(sender);
    userMessagesInformation.setReceiver(receiver);
    userMessagesInformation.setMessage(message);
    assertEquals(username, userMessagesInformation.getUsername());
    assertEquals(sender, userMessagesInformation.getSender());
    assertEquals(receiver, userMessagesInformation.getReceiver());
    assertEquals(group, userMessagesInformation.getGroup());
    assertEquals(message, userMessagesInformation.getMessage());
  }

  @Test
  public void userMessageParamsTest() {
    String userId = new ObjectId().toString();
    String messageId = new ObjectId().toString();
    userMessageParams.setUserId(userId);
    userMessageParams.setMessageId(messageId);
    assertEquals(userId, userMessageParams.getUserId());
    assertEquals(messageId, userMessageParams.getMessageId());
  }

  @Test
  public void userInformationTest() {
    String id = new ObjectId().toString();
    String username = "UserName";
    String name = "Name";
    List<String> followers = Collections.emptyList();
    List<String> following = Collections.emptyList();
    List<String> groups = Collections.emptyList();
    String isTracked = "true";
    userInformation.setId(id);
    userInformation.setUsername(username);
    userInformation.setName(name);
    userInformation.setGroups(groups);
    userInformation.setFollowing(following);
    userInformation.setFollowers(followers);
    userInformation.setIsTracked("true");
    assertEquals(id, userInformation.getId());
    assertEquals(username, userInformation.getUsername());
    assertEquals(name, userInformation.getName());
    assertEquals(groups, userInformation.getGroups());
    assertEquals(followers, userInformation.getFollowers());
    assertEquals(following, userInformation.getFollowing());
    assertEquals(isTracked, userInformation.getIsTracked());
  }

  @Test
  public void setUserCredentialsParamsTest() {
    String name = "Name";
    String username = "Username";
    String password = "Password";
    String oAuth = "false";

    userCredentialsParams.setName(name);
    userCredentialsParams.setUsername(username);
    userCredentialsParams.setPassword(password);
    userCredentialsParams.setName(name);
    userCredentialsParams.setOAuth(oAuth);

    assertEquals(name, userCredentialsParams.getName());
    assertEquals(username, userCredentialsParams.getUsername());
    assertEquals(password, userCredentialsParams.getPassword());
    assertEquals(oAuth, userCredentialsParams.getOAuth());
  }

  @Test
  public void setSenderReceiverParamsTest() {
    String senderId = new ObjectId().toString();
    String receiverId = new ObjectId().toString();
    senderReceiverParams.setReceiverId(receiverId);
    senderReceiverParams.setSenderId(senderId);

    assertEquals(receiverId, senderReceiverParams.getReceiverId());
    assertEquals(senderId, senderReceiverParams.getSenderId());
  }

}
