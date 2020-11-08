package fse.team2.common.websockets.commands;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.utils.Utils;
import fse.team2.common.websockets.commands.enums.ServerCommandType;

/**
 * This class represents a POJO for commands to be performed on the server.
 */
public class ServerCommand {

    private ServerCommandType type;
    private Message message;

    public ServerCommandType getType() {
        return type;
    }

    public void setType(ServerCommandType type) {
        this.type = type;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Check if this socket command instance is valid or not.
     *
     * @return - true if the socket command instance is valid.
     */
    public boolean isValid() {
        return (this.type != null && !Utils.checkForNull(message));
    }

    /**
     * Return a ServerSocketCommandBuilder instance.
     *
     * @return - an instance of SocketCommandBuilder
     */
    public static ServerSocketCommandBuilder getCommandBuilder() {
        return new ServerSocketCommandBuilder();
    }

    /**
     * This class builds SocketCommand instance using builder pattern.
     */
    public static class ServerSocketCommandBuilder {
        ServerCommand command;

        public ServerSocketCommandBuilder() {
            command = new ServerCommand();
        }

        public ServerSocketCommandBuilder setCommandType(ServerCommandType commandType) {
            command.setType(commandType);
            return this;
        }

        public ServerSocketCommandBuilder setMessage(Message message) {
            command.setMessage(message);
            return this;
        }

        /**
         * Return the SocketCommand object if it is valid.
         *
         * @return - SocketCommand instance created using this instance of builder.
         */
        public ServerCommand build() {
            if (command.isValid()) {
                return command;
            }
            throw new IllegalArgumentException("Socket command created is not valid");
        }
    }

}
