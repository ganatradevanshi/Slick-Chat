package fse.team2.common.websockets.commands;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientCommandTests {
    ClientCommand command;

    @Test
    public void clientCommandBuilderTest() {
        String messageContent = "Hello There";
        String senderName = "root";
        Message message = Message.messageBuilder()
                .setMessageContent(messageContent)
                .build();

        command = ClientCommand.getCommandBuilder()
                .setCommandType(ClientCommandType.RECEIVE_MESSAGE)
                .setMessage(message)
                .setSenderName(senderName)
                .build();

        assertEquals(ClientCommandType.RECEIVE_MESSAGE, command.getType());
        assertEquals(messageContent, command.getMessage().getContent());
        assertEquals(senderName, command.getSenderName());
    }

    @Test
    public void clientCommandBuilderTest2() {
        String messageContent = "Hello There";
        Message message = Message.messageBuilder()
                .setMessageContent(messageContent)
                .build();

        command = ClientCommand.getCommandBuilder()
                .setCommandType(ClientCommandType.SEND_MESSAGE_SUCCESS)
                .setMessage(message)
                .build();

        assertEquals(ClientCommandType.SEND_MESSAGE_SUCCESS
                , command.getType());
        assertEquals(messageContent, command.getMessage().getContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void clientNullTest1() {
        command = ClientCommand.getCommandBuilder()
                .setCommandType(null)
                .setMessage(new Message())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void clientNullTest2() {
        command = ClientCommand.getCommandBuilder()
                .setCommandType(ClientCommandType.RECEIVE_MESSAGE)
                .setMessage(null)
                .build();
    }
}
