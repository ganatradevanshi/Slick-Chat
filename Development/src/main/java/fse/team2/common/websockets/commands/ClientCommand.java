package fse.team2.common.websockets.commands;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.utils.Utils;
import fse.team2.common.websockets.commands.enums.ClientCommandType;

/**
 * This class represents a POJO for commands to be performed on the client.
 */
public class ClientCommand {

    private ClientCommandType type;
    private Message message;
    private String senderName;

    public ClientCommandType getType() {
        return type;
    }

    public void setType(ClientCommandType type) {
        this.type = type;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderName(){
        return this.senderName;
    }

    /**
     * Check if this socket command instance is valid or not.
     *
     * @return - true if the socket command instance is valid.
     */
    public boolean isValid() {
        return (this.type != null && !Utils.checkForNull(message));
    }

    public Message getMessage() {
        return message;
    }

    /**
     * Return a SocketCommandBuilder instance.
     *
     * @return - an instance of SocketCommandBuilder
     */
    public static ClientCommandBuilder getCommandBuilder() {
        return new ClientCommandBuilder();
    }

    /**
     * This class builds SocketCommand instance using builder pattern.
     */
    public static class ClientCommandBuilder {
        ClientCommand command;

        public ClientCommandBuilder() {
            command = new ClientCommand();
        }

        public ClientCommandBuilder setCommandType(ClientCommandType commandType) {
            command.setType(commandType);
            return this;
        }

        public ClientCommandBuilder setMessage(Message message) {
            command.setMessage(message);
            return this;
        }

        public ClientCommandBuilder setSenderName(String senderName) {
            command.setSenderName(senderName);
            return this;
        }

        /**
         * Return the SocketCommand object if it is valid.
         *
         * @return - SocketCommand instance created using this instance of builder.
         */
        public ClientCommand build() {
            if (command.isValid()) {
                return command;
            }
            throw new IllegalArgumentException("Socket command created is not valid");
        }
    }

}
