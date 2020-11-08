package com.neu.prattle.websocket.endpoints;

import com.mongodb.client.model.Filters;
import com.neu.prattle.service.dbservice.UserService;
import fse.team2.common.models.mongomodels.UserModel;
import fse.team2.common.websockets.constants.ServerSocketConstants;
import fse.team2.common.websockets.decoders.ServerCommandDecoder;
import fse.team2.common.websockets.encoders.ClientCommandEncoder;
import fse.team2.common.websockets.commands.ServerCommand;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client connects to this endpoint on login success.
 */
@ServerEndpoint(value = ServerSocketConstants.MAIN_ENDPOINT_URI + "{username}", decoders = ServerCommandDecoder.class, encoders = ClientCommandEncoder.class)
public class MainEndpoint {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final Set<String> onlineUsers = new HashSet<>();
    private static final HashMap<String, Session> userIdSessionMap = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        try {
            UserModel user = UserService.getInstance().findBy(Filters.eq(UserModel.USERNAME_FIELD, username)).next();
            onlineUsers.add(user.getUsername());
            sessions.add(session);
            userIdSessionMap.put(user.getId().toString(), session);
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, e.getMessage());
            userNotFound(session);
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.log(Level.INFO, "On error for main endpoint");
    }

    @OnMessage
    public void onMessage(Session session, ServerCommand command) {
        ServerCommandHandler.map.get(command.getType()).execute(session, userIdSessionMap, command);
    }

    /**
     * Perform necessary actions when user is not found with the
     * username which was used to establish the socket connection.
     *
     * @param session - session to be closed.
     */
    private void userNotFound(Session session) {
        try {
            onClose(session);
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "User with provided username could not be found"));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to close session:" + ex.getMessage());
        }
    }
}
