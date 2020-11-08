package fse.team2.slickclient.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import fse.team2.slickclient.constants.AppConstants;
import fse.team2.slickclient.model.ClientMessagePojo;
import fse.team2.slickclient.utils.LoggerService;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MessageServiceImpl implements MessageService {
    private static MessageServiceImpl singleInstance = null;
    private HttpService httpService;
    private Map<String, String> headers;
    private Map<String, String> bodyParams;

    private MessageServiceImpl(HttpService httpService) {
        this.httpService = httpService;
    }

    public static MessageServiceImpl getInstance(HttpService httpService) {
        if (singleInstance == null) {
            singleInstance = new MessageServiceImpl(httpService);
        }
        return singleInstance;
    }

    public static MessageServiceImpl getInstanceNonSingleton(HttpService httpService) {
        return new MessageServiceImpl(httpService);
    }

    @Override
    public List<ClientMessagePojo> getMessages(String senderId, String receiverIdOrGroup) {
        this.headers = new HashMap<>();
        this.bodyParams = new HashMap<>();
        this.bodyParams.put("senderId", senderId);
        this.bodyParams.put("receiverId", receiverIdOrGroup);
        Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.GET_MESSAGES_ENDPOINT, this.headers, this.bodyParams);
        ResponseBody responseBody = null;
        String messagesJSON;

        try {
            if (response != null && response.code() == HttpStatus.SC_OK) {
                responseBody = response.body();
                messagesJSON = responseBody.string();
                return parseJSONToMessages(messagesJSON);
            }
        } catch (IOException | IllegalStateException e) {
            LoggerService.log(Level.SEVERE, "Failed to get messages: " + e.getMessage());
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean deleteMessage(String userId, String messageId) {
        this.bodyParams = new HashMap<>();
        this.bodyParams.put("userId", userId);
        this.bodyParams.put("messageId", messageId);
        Response response = httpService.sendPOST(AppConstants.HOSTNAME + AppConstants.DELETE_MESSAGE_ENDPOINT, headers, this.bodyParams);
        return response.code() == HttpStatus.SC_OK;
    }

    /**
     * Converts a list of messages from JSON-String to {@code List<ClientMessagePojo>}
     *
     * @param messagesJSON list of messages as JSON-String
     * @return list of messages as {@code List<ClientMessagePojo>}
     */
    private List<ClientMessagePojo> parseJSONToMessages(String messagesJSON) {
        Gson gson = new Gson();
        Type userType = new TypeToken<List<ClientMessagePojo>>() {
        }.getType();
        return gson.fromJson(messagesJSON, userType);
    }
}
