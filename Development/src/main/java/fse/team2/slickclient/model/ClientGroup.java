package fse.team2.slickclient.model;

public interface ClientGroup {

  /**
   * Create a group.
   *
   * @param name -> The name of the group which would be created.
   */
  void createGroup(String name);

  /**
   * Delete a group.
   *
   * @param groupId -> The id of the group which is supposed to be deleted.
   */
  void deleteGroup(String groupId);

  /**
   * Add  User to a group.
   *
   * @param groupId -> The id of the group in which we are supposed to add the User.
   * @param userId  -> The id of the User who is supposed to be added.
   */
  void addUserToGroup(String groupId, String userId);

  /**
   * Remove User from a group.
   *
   * @param groupId -> The id of the group from which we are supposed to remove the User.
   * @param userId  -> The id of the User who is supposed to be removed.
   */
  void removeUserFromGroup(String groupId, String userId);

  /**
   * Assigns a User the role of Moderator of the Group.
   *
   * @param groupId -> The id of thr group of which we would assign a moderator.
   * @param userId  -> The id of the User who we would assign as moderator of the group.
   */
  void assignModerator(String groupId, String userId);
}
