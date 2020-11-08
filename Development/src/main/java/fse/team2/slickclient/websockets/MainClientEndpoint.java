package fse.team2.slickclient.websockets;

import fse.team2.common.websockets.decoders.ClientCommandDecoder;
import fse.team2.common.websockets.encoders.ServerCommandEncoder;
import fse.team2.common.websockets.commands.ClientCommand;

import javax.websocket.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ClientEndpoint(decoders = ClientCommandDecoder.class, encoders = ServerCommandEncoder.class)
public class MainClientEndpoint {

    private static final Logger logger = Logger.getLogger(MainClientEndpoint.class.getName());

    @OnOpen
    public void onOpen(Session session) {
        logger.log(Level.INFO, "Client connected with session id " + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        logger.log(Level.INFO, "Client closed: " + reason.toString());
        try {
            session.close(reason);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // This will be implemented in the future.
    }

    @OnMessage
    public void onMessage(Session session, ClientCommand command) {
        ClientCommandHandler.map.get(command.getType()).execute(session, null, command);
    }

}
