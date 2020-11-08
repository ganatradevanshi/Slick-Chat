package fse.team2.common.websockets.commands;

import javax.websocket.Session;
import java.util.HashMap;

/**
 * This interface represents a CommandExecutor meant to execute a Server-side or client-side commands.
 */
public interface CommandExecutor<T> {

    /**
     * Execute the Server-side command.
     *
     * @param senderSession - session of the client who initiated the command.
     * @param sessions      - sessions currently connected to the socket.
     * @param command       - command initiated by the sender.
     * @return - true if the command was executed successfully.
     */
    boolean execute(Session senderSession, HashMap<String, Session> sessions, T command);
}
