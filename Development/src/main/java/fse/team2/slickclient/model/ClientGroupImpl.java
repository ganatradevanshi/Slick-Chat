package fse.team2.slickclient.model;

import fse.team2.slickclient.services.GroupService;
import fse.team2.slickclient.services.UserService;
import fse.team2.slickclient.utils.LoggerService;

import java.util.logging.Level;

public class ClientGroupImpl extends ClientGroupPojo implements ClientGroup {
  private StringBuilder log;
  private GroupService groupService;
  private UserService userService;

  public ClientGroupImpl() {
    // This public constructor is needed by the gson to map documents to user POJO.
  }

  public ClientGroupImpl(StringBuilder log, GroupService groupService, UserService userService) {
    this.log = log;
    this.groupService = groupService;
    this.userService = userService;
  }

  @Override
  public void createGroup(String name) {
    boolean isGroupCreated = false;
    if (this.userService.isUserLoggedIn()) {
      isGroupCreated = this.groupService.createGroup(name);
      LoggerService.log(Level.INFO, "Group Created Successfully: " + isGroupCreated);
    } else {
      LoggerService.log(Level.WARNING, "You must login to create a group!");
    }
    log.append(isGroupCreated ? "Group Creation Successful\n" : "Group Creation Unsuccessful\n");
  }

  @Override
  public void deleteGroup(String groupId) {
    boolean isGroupDeleted = false;
    if (this.userService.isUserLoggedIn()) {
      isGroupDeleted = this.groupService.deleteGroup(groupId);
      LoggerService.log(Level.INFO, "Group Deleted Successfully: " + isGroupDeleted);
    } else {
      LoggerService.log(Level.WARNING, "You must login to delete a group!");
    }
    log.append(isGroupDeleted ? "Group Deletion Successful\n" : "Group Deletion Unsuccessful\n");
  }

  @Override
  public void addUserToGroup(String groupId, String userId) {
    boolean isUserAddedToGroup = false;
    if (this.userService.isUserLoggedIn()) {
      isUserAddedToGroup = this.groupService.addUserToGroup(groupId, userId);
      LoggerService.log(Level.INFO, "User Added Successfully: " + isUserAddedToGroup);
    } else {
      LoggerService.log(Level.WARNING, "You must login to add a user to a group!");
    }
    log.append(isUserAddedToGroup ? "User Addition Successful\n" : "User Addition Unsuccessful\n");
  }

  @Override
  public void removeUserFromGroup(String groupId, String userId) {
    boolean isUserRemovedFromGroup = false;
    if (this.userService.isUserLoggedIn()) {
      isUserRemovedFromGroup = this.groupService.removeUserFromGroup(groupId, userId);
      LoggerService.log(Level.INFO, "User Removed Successfully: " + isUserRemovedFromGroup);
    } else {
      LoggerService.log(Level.WARNING, "You must login to remove a user from a group!");
    }
    log.append(isUserRemovedFromGroup ? "User Removal Successful\n" : "User Removal Unsuccessful\n");
  }

  @Override
  public void assignModerator(String groupId, String userId) {
    boolean isModeratorAssigned = false;
    if (this.userService.isUserLoggedIn()) {
      isModeratorAssigned = this.groupService.addModerator(groupId, userId);
      LoggerService.log(Level.INFO, "Moderator assigned  Successfully: " + isModeratorAssigned);
    } else {
      LoggerService.log(Level.WARNING, "You must login to assign a moderator to a group!");
    }
    log.append(isModeratorAssigned ? "Moderator Assignment Successful\n" : "Moderator Assignment Unsuccessful\n");
  }
}
