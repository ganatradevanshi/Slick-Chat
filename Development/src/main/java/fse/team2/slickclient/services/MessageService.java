package fse.team2.slickclient.services;

import java.util.List;

import fse.team2.slickclient.model.ClientMessagePojo;

public interface MessageService {

  /**
   * Gets a list of all messages exchanged between currently logged in user and another user or group.
   * @param receiverIdOrGroup id of another user or group to get messages for.
   * @return List of messages as {@code List<ClientMessagePojo>}
   */
  List<ClientMessagePojo> getMessages(String senderId, String receiverIdOrGroup);

  /**
   * Deletes a message with this messageId.
   * @param messageId object id of the message to be deleted.
   * @return true if message is deleted successfully; false otherwise.
   */
  boolean deleteMessage(String senderId, String messageId);
}
