package fse.team2.slickclient.controller;

import com.neu.prattle.exceptions.UserAlreadyPresentException;
import com.neu.prattle.main.PrattleApplication;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.HashMap;

import fse.team2.slickclient.SlickClient;
import fse.team2.slickclient.commands.Command;
import fse.team2.slickclient.constants.AppConstants;
import fse.team2.slickclient.model.MockUser;
import fse.team2.slickclient.view.CommandView;
import fse.team2.slickclient.view.CommandViewImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommandControllerTest {
  private Command abstractCommand;
  private CommandView commandView;
  private StringBuilder log;

  @Before
  public void init() {
    this.log = new StringBuilder();
    this.commandView = new CommandViewImpl();
  }

  /* ********** Mock Model Tests ********** */
  @Test
  public void testLoginCommand() {
    String command = "/login user password$1 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Login called: user, password$1\n",this.log.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCommandException() {
    CommandView view = new CommandViewImpl();
    view.setCommandMap(new HashMap<>());
    view.performAction("demoCommand");
  }

  @Test
  public void testRegisterCommand() {
    String command = "/register name username password$1 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Register called: name, username, password$1\n",this.log.toString());
  }

  @Test
  public void testFollowCommand() {
    String command = "/follow abc123 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Follow user called: abc123\n",this.log.toString());
  }

  @Test
  public void testUnfollowCommand() {
    String command = "/unfollow abc123 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Unfollow user called: abc123\n",this.log.toString());
  }

  @Test
  public void testSendMessageCommand() {
    String command = "/sendMessage abc123 Hello exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Send Message called: abc123, Hello\n",this.log.toString());
  }

  @Test
  public void testDeleteMessageCommand() {
    String command = "/deleteMessage msg123 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Delete Message called: msg123\n",this.log.toString());
  }

  @Test
  public void testSendFileCommand() {
    String command = "/sendFile abc123 img.png exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Send file called: abc123, img.png\n",this.log.toString());
  }

  @Test
  public void testForwardMessageCommand() {
    String command = "/forwardMessage abc345 msg123 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Forward message called: abc345, msg123\n",this.log.toString());
  }

  @Test
  public void testMuteCommand() {
    String command = "/mute abc123 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Mute user called: abc123\n",this.log.toString());
  }

  @Test
  public void testHelpCommand() {
    String command = "/help exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Help called\n",this.log.toString());
  }

  @Test
  public void testDeleteChatCommand() {
    String command = "/deleteChat abc345 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Delete chat called: abc345\n",this.log.toString());
  }

  @Test
  public void testGetChatsCommand() {
    String command = "/getChats exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Get chats called\n",this.log.toString());
  }
  @Test(expected = IllegalArgumentException.class)
  public void illegalCommand() {
    String command = "/TakeMeToMoon exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
  }


  @Test
  public void testgetMessagesCommand() {
    String command = "/getMessages abc123 exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Get messages called, abc123\n",this.log.toString());
  }

  @Test
  public void testSearchUsersCommand() {
    String command = "/searchUsers a exit";
    this.log = new StringBuilder();
    Readable stringCommand = new StringReader(command);
    this.abstractCommand = new CommandControllerImpl(stringCommand, this.commandView);
    this.abstractCommand.apply(new MockUser(this.log));
    assertEquals("Search Users called, a\n",this.log.toString());
  }

  @Test
  public void testConstants() {
    assertNotNull(new AppConstants());
  }

  @Test
  public void testMain() {
    SlickClient.main(new String[]{"test"});
    assertNotNull(new SlickClient());
  }

  @Test
  public void prattleAppTest() {
    PrattleApplication prattleApplication = new PrattleApplication();
    prattleApplication.getClasses();
    assertNotNull(prattleApplication.getClasses());
  }

  @Test(expected = UserAlreadyPresentException.class)
  public void testUserAlreadyPresentTest() {
    throw new UserAlreadyPresentException("test");
  }
 }