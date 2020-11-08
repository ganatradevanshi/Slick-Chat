package fse.team2.common.websockets.decoders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import org.bson.types.ObjectId;

import fse.team2.common.websockets.commands.ClientCommand;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a decoder used to convert JSON to ClientCommand POJO.
 */
public class ClientCommandDecoder implements Decoder.Text<ClientCommand> {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, (JsonSerializer<ObjectId>) (src, typeOfSrc, context) -> new JsonPrimitive(src.toHexString())).registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) (json, typeOfT, context) -> new ObjectId(json.getAsString())).create();
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public ClientCommand decode(String json) throws DecodeException {
        try {
            return gson.fromJson(json, ClientCommand.class);
        } catch (JsonParseException e) {
            logger.log(Level.SEVERE, "JSON provided for SocketCommand is not valid :: " + json);
            throw new DecodeException(json, "JSON provided for SocketCommand is not valid");
        }
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Implementation will be added in the future.
    }

    @Override
    public void destroy() {
        logger.log(Level.INFO, "Decoder Destroy");
    }
}
