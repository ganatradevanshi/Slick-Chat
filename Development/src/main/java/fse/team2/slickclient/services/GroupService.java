package fse.team2.slickclient.services;

public interface GroupService {

  /**
   * Executes creating a Group on the server via HttpService.
   *
   * @param name -> The name of Group which
   * @return -> true if creation is successful, false otherwise
   */
  boolean createGroup(String name);

  /**
   * Executes deleting a Group on the server via HttpService.
   *
   * @param groupId -> The id of the Group which is supposed to be deleted.
   * @return -> true if deletion is successful, false otherwise
   */
  boolean deleteGroup(String groupId);

  /**
   * Executes adding a User to the Group
   *
   * @param groupId -> The id of the group in which User would be added.
   * @param userId  ->  The id of the user who is supposed to be added.
   * @return -> true if adding a user is successful, false otherwise
   */
  boolean addUserToGroup(String groupId, String userId);

  /**
   * Executes removing a User from the Group
   *
   * @param groupId -> The id of the group from which User would be removed.
   * @param userId  -> The id of the user who is supposed to be removed.
   * @return -> true if removing a user is successful, false otherwise
   */
  boolean removeUserFromGroup(String groupId, String userId);

  /**
   * Executes assigning a moderator for the Group
   *
   * @param groupId -> The id of the Group for which we would assign a moderator
   * @param userId  -> The id of the User whom we would assign as moderator
   * @return true if assigning a user as moderator is successful, false otherwise.
   */
  boolean addModerator(String groupId, String userId);
}
