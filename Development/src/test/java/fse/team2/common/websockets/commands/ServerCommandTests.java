package fse.team2.common.websockets.commands;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.websockets.commands.enums.ServerCommandType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerCommandTests {
    ServerCommand command;

    @Test
    public void serverCommandBuilderTest1() {
        String messageContent = "Hello There";
        Message message = Message.messageBuilder()
                .setMessageContent(messageContent)
                .build();

        command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(message)
                .build();

        assertEquals(ServerCommandType.SEND_MESSAGE, command.getType());
        assertEquals(messageContent, command.getMessage().getContent());
    }

    @Test
    public void serverCommandBuilderTest2() {
        String messageContent = "Hello There";
        Message message = Message.messageBuilder()
                .setMessageContent(messageContent)
                .build();

        command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.FORWARD_MESSAGE)
                .setMessage(message)
                .build();

        assertEquals(ServerCommandType.FORWARD_MESSAGE, command.getType());
        assertEquals(messageContent, command.getMessage().getContent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void serverCommandNullTest1() {
        command = ServerCommand.getCommandBuilder()
                .setCommandType(null)
                .setMessage(new Message())
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void serverCommandNullTest2() {
        command = ServerCommand.getCommandBuilder()
                .setCommandType(ServerCommandType.SEND_MESSAGE)
                .setMessage(null)
                .build();
    }
}
