package com.neu.prattle.websocket.endpoints;

import com.neu.prattle.service.dbservice.GroupService;
import com.neu.prattle.service.dbservice.MessageService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.service.encryptionservice.EncryptionService;
import fse.team2.common.models.mongomodels.Group;
import fse.team2.common.models.mongomodels.Message;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.websockets.commands.*;
import fse.team2.common.websockets.commands.enums.ClientCommandType;
import fse.team2.common.websockets.commands.enums.ServerCommandType;
import fse.team2.slickclient.utils.LoggerService;
import org.bson.types.ObjectId;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a command handler whose job is to maintain a map
 * from ServerCommandType to the action to be perform for that command type.
 */
class ServerCommandHandler {

    private static final Logger logger = Logger.getLogger(ServerCommandHandler.class.getName());

    protected static final EnumMap<ServerCommandType, CommandExecutor<ServerCommand>> map = new EnumMap(ServerCommandType.class);

    private ServerCommandHandler() {
    }

    static {
        map.put(ServerCommandType.SEND_MESSAGE, ServerCommandHandler::sendMessage);

        map.put(ServerCommandType.FORWARD_MESSAGE, (senderSession, receiverSession, command) -> {
            command.getMessage().setForwarded(true);
            return sendMessage(senderSession, receiverSession, command);
        });
    }

    /**
     * Make a write for message in the database and send message in form of ClientCommand
     * to the receiver if it is online.
     *
     * @param senderSession    - session of the initiator of the server command.
     * @param userIdSessionMap - session to userId map
     * @param command          - command to be performed.
     */
    private static boolean sendMessage(Session senderSession, HashMap<String, Session> userIdSessionMap, ServerCommand command) {
        addMessageToDb(command);

        if (command.getMessage().isGroupMessage()) {
            return sendGroupMessage(senderSession, userIdSessionMap, command);
        }

        boolean messageSuccess = sendMessageToReceiver(getSessionFromUserId(userIdSessionMap,
                command.getMessage().getReceiverId().toString()), command);
        if (messageSuccess) {
            try {
                sendFeedback(senderSession, command);
            } catch (IOException | EncodeException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return false;
            }
        }
        return messageSuccess;
    }

    /**
     * Send message to all members of the group.
     *
     * @param senderSession    - session of the party that initiated server command.
     * @param userIdSessionMap - map of userId to session objects.
     * @param command          - command to be performed.
     * @return - return true if operation is successful
     */
    private static boolean sendGroupMessage(Session senderSession, HashMap<String, Session> userIdSessionMap, ServerCommand command) {
        Message message = command.getMessage();
        ObjectId groupId = message.getReceiverId();
        Group group = GroupService.getInstance().findById(groupId);
        Iterator<ObjectId> it = group.getUsers().iterator();
        while (it.hasNext()) {
            // Send message to every member of the group excluding the sender.
            String userId = it.next().toString();
            if (!userId.equals(message.getSenderId().toString())) {
                sendMessageToReceiver(getSessionFromUserId(userIdSessionMap, userId), command);
            }
        }

        try {
            sendFeedback(senderSession, command);
        } catch (IOException | EncodeException e) {
            logger.log(Level.SEVERE, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Send message to the receiver.
     *
     * @param receiverSession - session of the party that should receive the message.
     * @param command         - command to be performed.
     * @return - return true if operation is successful
     */
    private static boolean sendMessageToReceiver(Session receiverSession, ServerCommand command) {
        if (receiverSession != null) {
            // Send message to receiver user client.
            LoggerService.log(Level.INFO, "Receiver session found");
            try {
                pushMessage(receiverSession, command);
                return true;
            } catch (IOException | EncodeException e) {
                logger.log(Level.SEVERE, e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Add message to the database.
     *
     * @param command - command to be performed.
     * @return - return true if operation is successful
     */
    private static void addMessageToDb(ServerCommand command) {
        // Set id to the message of the command
        Message message = command.getMessage();
        if (message.getId() == null) {
            message.setId(new ObjectId());
        }
        // If thread head is empty, set thread head to id of the message
        if (message.getThreadHead() == null) {
            message.setThreadHead(message.getId());
        }

        // Set Default encryption to BASIC if encryption level is not set.
        if (message.getEncryptionLevel() == null) {
            message.setEncryptionLevel(EncrpytionLevel.BASIC);
        }

        Message messageCopy = Message.messageBuilder()
                .setGroupMessage(message.isGroupMessage())
                .setTags(message.getTags())
                .setId(message.getId())
                .setSenderId(message.getSenderId())
                .setEncryptionLevel(message.getEncryptionLevel())
                .setExpiryDate(message.getExpiryDate())
                .setTimestamp(message.getTimestamp())
                .setReceiverId(message.getReceiverId())
                .setMessageContent(message.getContent())
                .setDeleted(message.isDeleted())
                .setHidden(message.isHidden())
                .setForwarded(message.isForwarded())
                .setMessageType(message.getMessageType())
                .setThreadHead(message.getThreadHead())
                .build();

        // Encrypt the message content
        EncryptionService service = messageCopy.getEncryptionLevel().getEncryptionService();
        messageCopy.setContent(service.encrypt(messageCopy.getContent()));

        // Add message to database
        LoggerService.log(Level.INFO, "Message on server: " + command.getMessage().getContent());
        MessageService.getInstance().add(messageCopy);
    }

    /**
     * Push message to relevant online users.
     *
     * @param receiverSession - session of the party to whom the message is to be pushed.
     * @param command         - command to be performed.
     * @return - return true if operation is successful
     */
    private static void pushMessage(Session receiverSession, ServerCommand command) throws IOException, EncodeException {
        String senderName = null;
        try {
            ObjectId senderId = command.getMessage().getSenderId();
            UserModel user = UserService.getInstance().findById(senderId);
            senderName = user.getUsername();
        } catch (Exception e) {
            LoggerService.log(Level.SEVERE, "User could not be found!");
        }
        ClientCommand receiverCommand = ClientCommand.getCommandBuilder()
                .setCommandType(ClientCommandType.RECEIVE_MESSAGE)
                .setMessage(command.getMessage())
                .setSenderName(senderName)
                .build();

        receiverSession.getBasicRemote().sendObject(receiverCommand);
    }

    /**
     * Send feedback to the initiator of the command.
     *
     * @param senderSession - session object of the initiator of the command.
     * @param command       - command to be handled by the main server socket.
     * @throws IOException
     * @throws EncodeException
     */
    private static void sendFeedback(Session senderSession, ServerCommand command) throws IOException, EncodeException {
        ClientCommand senderCommand = ClientCommand.getCommandBuilder()
                .setCommandType(ClientCommandType.SEND_MESSAGE_SUCCESS)
                .setMessage(command.getMessage())
                .build();

        senderSession.getBasicRemote().sendObject(senderCommand);
    }

    /**
     * Retrieve session object for the user id provided.
     *
     * @param userIdSessionMap - map of user id to session objects.
     * @param userId           - id of the user
     * @return - session object corresponding to the userId provided.
     */
    private static Session getSessionFromUserId(HashMap<String, Session> userIdSessionMap, String userId) {
        Session session = null;
        if (userIdSessionMap.containsKey(userId)) {
            session = userIdSessionMap.get(userId);
        }
        return session;
    }
}
