package com.neu.prattle.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mongodb.client.model.Filters;
import com.neu.prattle.service.dbservice.*;
import com.neu.prattle.service.encryptionservice.EncryptionService;
import com.neu.prattle.service.mediaservice.AWSMediaService;
import com.neu.prattle.service.mediaservice.MediaService;
import com.neu.prattle.utils.JWTUtils;

import fse.team2.common.models.controllermodels.AddPollResponseParams;
import fse.team2.common.models.controllermodels.CreatePollMessageParams;
import fse.team2.common.models.controllermodels.RemovePollResponseParams;
import fse.team2.common.models.mongomodels.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fse.team2.common.models.controllermodels.CreateMediaMessageParams;
import fse.team2.common.models.mongomodels.enums.EncrpytionLevel;
import fse.team2.common.models.mongomodels.enums.MessageType;
import fse.team2.common.utils.adapters.ObjectIdTypeAdapter;

/**
 * This class exposes REST APIs for {@link Message} resource.
 */
@Path(value = "/message")
public class MessageController {
    private DatabaseService<Message> messageService;
    private MediaService mediaService;
    private DatabaseService<UserModel> userService;
    private DatabaseService<Group> groupService;

    /**
     * Creates a {@link MessageController} instance with the default {@link MessageService}.
     */
    public MessageController() {
        messageService = MessageService.getInstance();
        mediaService = AWSMediaService.getInstance();
        userService = UserService.getInstance();
        groupService = GroupService.getInstance();
    }

    /**
     * Creates a {@link MessageController} instance with the provided {@link MessageService} instance
     * and {@link MediaService} instance
     */
    public MessageController(DatabaseService<Message> messageService, MediaService mediaService) {
        this.messageService = messageService;
        this.mediaService = mediaService;
    }

    /**
     * This helper method takes in an Authentication Header and gives corresponding userId.
     *
     * @param authHeader The authentication header passed from the HTTP Request
     * @return userId if token is decoded successfully
     */
    private String getIdfromAuthHeader(String authHeader) {
        return JWTUtils.validateJWToken(authHeader.replace("Bearer ", ""));
    }

    /**
     * Delete a message with the messageId provided.
     *
     * @param messageId  Id of the message which is supposed to be deleted
     * @param authHeader The Authentication Header from which we would decode the token to get the
     *                   User who sent the request
     * @return ok response if deleted successfully, returns error response code otherwise.
     */
    @DELETE
    @Path(value = "/account/delete-message/{messageId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteMessage(@PathParam("messageId") String messageId,
                                  @HeaderParam("Authorization") String authHeader) {
        String senderId = getIdfromAuthHeader(authHeader);
        // the token was not decoded successfully
        if (senderId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserModel sender = userService.findById(new ObjectId(senderId));
        Message message = messageService.findById(new ObjectId(messageId));

        if (sender == null || message == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // set deleted to true
        message.setDeleted(true);
        // replace the message object in DB
        messageService.replaceOne(Filters.eq(Message.ID_FIELD, message.getId()), message);

        return Response.ok().build();
    }

    /**
     * Retrieve messages between two users.
     *
     * @param receiverID -> Id of the User/Group for whom we want to retrieve the messages for.
     * @param authHeader -> The Authentication Header from which we would decode the token to get the
     *                   User who sent this request.
     * @return -> response containing all the found messages.
     */
    @GET
    @Path(value = "/account/get-messages/{receiverID}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllMessages(@PathParam("receiverID") String receiverID,
                                   @HeaderParam("Authorization") String authHeader) {
        String id = getIdfromAuthHeader(authHeader);
        // the token was not decoded successfully
        if (id == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }


        UserModel selfUser = userService.findById(new ObjectId(id));
        if (selfUser == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        ObjectId receiver = new ObjectId(receiverID);
        UserModel user = userService.findById(receiver);
        Group group = groupService.findById(receiver);
        // check if such a user/group is present
        if (user == null && group == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<Message> messages;
        Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
        try {
            messages = getAllMessagesHelper(id, receiverID);
        } catch (Exception e) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String json = gson.toJson(messages);
        return Response.ok(json, MediaType.APPLICATION_JSON).build();
    }

    /**
     * Retrieve messages for a group
     *
     * @param receiverID - id of the group
     * @param authHeader - The Authentication Header from which we would decode the token to get the
     *                   User who sent this request.
     * @return - response containing all the found messages.
     */
    @GET
    @Path(value = "/account/get-group-messages/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllGroupMessages(@PathParam("groupId") String receiverID,
                                        @HeaderParam("Authorization") String authHeader) {
        String id = getIdfromAuthHeader(authHeader);
        Group group = groupService.findById(new ObjectId(receiverID));

        if (group == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // Check if user exists in the group
        if (!group.getUsers().contains(new ObjectId(id))) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            Iterator<Message> it = messageService.findBy(Filters.eq(Message.RECEIVER_ID_FIELD, new ObjectId(receiverID)));
            List<Message> messages = new ArrayList<>();
            while (it.hasNext()) {
                Message message = it.next();
                if (message.getThreadHead() == null) {
                    message.setThreadHead(message.getId());
                }
                if (message.getUserData() == null) {
                    ObjectId receiverId = message.getReceiverId();
                    UserModel receiver = userService.findById(receiverId);
                    List<UserModel> userData = new ArrayList<>();
                    if (receiver != null) {
                        userData.add(receiver);
                    }
                    message.setUserData(userData);
                }

                UserModel sender = userService.findById(message.getSenderId());
                if (sender != null) {
                    message.setSenderName(sender.getName());
                }
                messages.add(message);
            }
            messages = getDecryptedMessages(messages);
            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(messages);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
    }

    /**
     * API to create a media type of message.
     *
     * @param params - information required to create the message.
     * @return - media type of message.
     */
    @POST
    @Path(value = "/media")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMediaMessage(CreateMediaMessageParams params) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(params.getFileContents());
            ObjectId messageId = new ObjectId();
            String url = mediaService.upload(decodedBytes, messageId.toString() + "_" + params.getFileName());
            Message message = Message.messageBuilder()
                    .setMessageType(MessageType.MEDIA)
                    .setMessageContent(url)
                    .setSenderId(new ObjectId(params.getSenderId()))
                    .setId(messageId)
                    .setReceiverId(new ObjectId(params.getReceiverId()))
                    .setEncryptionLevel(EncrpytionLevel.BASIC)
                    .setTimestamp(new Date())
                    .setExpiryDate(new Date())
                    .setTags(new ArrayList<>())
                    .build();
            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();

            String json = gson.toJson(message);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a poll type of message.
     *
     * @param params - information required to create a poll message.
     * @return - poll type of message.
     */
    @POST
    @Path(value = "/poll")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPollMessage(CreatePollMessageParams params) {
        try {
            ObjectId messageId = new ObjectId();
            Message message = Message.messageBuilder()
                    .setMessageType(MessageType.POLL)
                    .setMessageContent(params.getPollQuestion())
                    .setSenderId(new ObjectId(params.getSenderId()))
                    .setId(messageId)
                    .setReceiverId(new ObjectId(params.getReceiverId()))
                    .setEncryptionLevel(EncrpytionLevel.BASIC)
                    .setTimestamp(new Date())
                    .setExpiryDate(new Date())
                    .setTags(new ArrayList<>())
                    .build();

            Poll poll = new Poll();
            poll.setResponses(new ArrayList<>());
            poll.setOptions(params.getOptions());
            poll.setMessageId(messageId);
            PollService.getInstance().add(poll);

            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(message);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * API to respond to a poll.
     *
     * @param params - params includes user id and the response to the poll.
     * @return - poll type of message.
     */
    @POST
    @Path(value = "/poll/response/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPollResponse(AddPollResponseParams params) {
        try {
            ObjectId messageId = new ObjectId(params.getMessageId());
            Poll poll = PollService.getInstance().findById(messageId);
            UserResponse response = new UserResponse();
            response.setUserId(new ObjectId(params.getUserId()));
            response.setResponse(params.getResponse());
            List<UserResponse> responses = poll.getResponses();
            responses.add(response);
            poll.setResponses(responses);

            Poll updatedPoll = new Poll();
            updatedPoll.setMessageId(poll.getMessageId());
            updatedPoll.setOptions(poll.getOptions());
            updatedPoll.setResponses(poll.getResponses());

            PollService.getInstance().replaceOne(Filters.eq(Poll.MESSAGE_ID_FIELD, messageId), updatedPoll);
            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(poll);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * API to undo a response to a poll
     *
     * @param params - params to mention user id.
     */
    @POST
    @Path(value = "/poll/delete-response/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removePollResponse(RemovePollResponseParams params) {
        try {
            ObjectId messageId = new ObjectId(params.getMessageId());
            Poll poll = PollService.getInstance().findById(messageId);
            List<UserResponse> responses = poll.getResponses();
            UserResponse responseToDelete = null;

            Iterator<UserResponse> it = responses.iterator();
            while (it.hasNext()) {
                UserResponse curResponse = it.next();
                if (curResponse.getUserId().toString().equals(params.getUserId())) {
                    responseToDelete = curResponse;
                }
            }
            if (responseToDelete != null) {
                responses.remove(responseToDelete);
                poll.setResponses(responses);

                Poll updatedPoll = new Poll();
                updatedPoll.setMessageId(poll.getMessageId());
                updatedPoll.setOptions(poll.getOptions());
                updatedPoll.setResponses(poll.getResponses());

                PollService.getInstance().replaceOne(Filters.eq(Poll.MESSAGE_ID_FIELD, messageId), updatedPoll);
            }

            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(poll);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * API to retrieve a poll
     *
     * @param messageId - id of the poll/message.
     */
    @GET
    @Path(value = "/poll/{messageId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPoll(@PathParam("messageId") String messageId) {
        try {
            ObjectId id = new ObjectId(messageId);
            Poll poll = PollService.getInstance().findById(id);

            Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIdTypeAdapter()).create();
            String json = gson.toJson(poll);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper function to retrieve all the messages between two user ids.
     *
     * @param user1 - first user of the conversation.
     * @param user2 - second user of the conversation.
     * @return - list of {@link Message} that are part of the conversation.
     */
    private List<Message> getAllMessagesHelper(String user1, String user2) {
        List<Message> output = new ArrayList<>();
        Iterator<Message> resultIterator1;
        Iterator<Message> resultIterator2;

        resultIterator1 = messageService.findBy(Filters.and(
                Filters.eq(Message.SENDER_ID_FIELD, new ObjectId(user1)),
                Filters.eq(Message.RECEIVER_ID_FIELD, new ObjectId(user2))));

        resultIterator2 = messageService.findBy(Filters.and(
                Filters.eq(Message.SENDER_ID_FIELD, new ObjectId(user2)),
                Filters.eq(Message.RECEIVER_ID_FIELD, new ObjectId(user1))));

        while (resultIterator1.hasNext()) {
            output.add(resultIterator1.next());
        }

        while (resultIterator2.hasNext()) {
            output.add(resultIterator2.next());
        }

        return getDecryptedMessages(output);
    }

    private List<Message> getDecryptedMessages(List<Message> messages) {
        for (Message message : messages) {
            EncryptionService service = message.getEncryptionLevel().getEncryptionService();
            String decrypted = service.decrypt(message.getContent());
            if (decrypted != null) {
                message.setContent(decrypted);
            }
        }
        return messages;
    }

}
