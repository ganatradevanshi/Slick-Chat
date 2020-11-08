package fse.team2.slickclient.websockets;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Level;

import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.websockets.commands.ClientCommand;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
import fse.team2.common.websockets.commands.CommandExecutor;
import fse.team2.slickclient.utils.LoggerService;

import javax.websocket.Session;

/**
 * This class represents a command handler whose job is to maintain a map from ClientCommandType to
 * the action to be perform for that command type.
 */
class ClientCommandHandler {

    protected static final EnumMap<ClientCommandType, CommandExecutor<ClientCommand>> map = new EnumMap(ClientCommandType.class);

    static {
        map.put(ClientCommandType.RECEIVE_MESSAGE, ClientCommandHandler::receiveMessage);
        map.put(ClientCommandType.SEND_MESSAGE_SUCCESS, ClientCommandHandler::sendMessageSuccess);
    }

    private ClientCommandHandler() {

    }

    /**
     * Perform necessary operations on receiving a message.
     *
     * @param senderSession - session object for the receiver of the message.
     * @param command       - command to be performed.
     */
    private static boolean receiveMessage(Session senderSession, HashMap<String, Session> sessions, ClientCommand command) {
        Message message = command.getMessage();
        LoggerService.log(Level.INFO, "Message from: " + message.getSenderId() + " Type: " + message.getMessageType() + " content: " + message.getContent());
        return true;
    }

    /**
     * Perform necessary operations on message send success.
     *
     * @param senderSession - session object for the receiver of the message.
     * @param command       - command to be performed.
     */
    private static boolean sendMessageSuccess(Session senderSession, HashMap<String, Session> sessions, ClientCommand command) {
        Message message = command.getMessage();
        LoggerService.log(Level.INFO, "Message (message id: " + message.getId() + " )" +
                " sent successfully to: " + message.getReceiverId() + " : " + message.getContent());
        return true;
    }


}
