package fse.team2.common.websockets.encoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;
import fse.team2.common.websockets.commands.ClientCommand;
import org.bson.types.ObjectId;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents an encoder used to convert ClientCommand POJO to JSON.
 */
public class ClientCommandEncoder implements Encoder.Text<ClientCommand> {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public String encode(ClientCommand command) throws EncodeException {
        try {
            return gson.toJson(command);
        } catch (JsonParseException e) {
            throw new EncodeException(command, "Command object provided is not valid. Failed to convert to Json.");
        }
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Implementation will be added in the future.
    }

    @Override
    public void destroy() {
        logger.log(Level.INFO, "Encoder Destroy");
    }
}
