package com.neu.prattle.controllertests;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import com.neu.prattle.controller.GroupController;
import com.neu.prattle.service.dbservice.DatabaseService;
import com.neu.prattle.service.dbservice.GroupService;
import com.neu.prattle.service.dbservice.UserService;
import com.neu.prattle.utils.JWTUtils;

import fse.team2.common.models.controllermodels.UserCredentialsParams;
import fse.team2.common.models.mongomodels.AuthenticationData;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Response;

import fse.team2.common.models.mongomodels.Group;
import fse.team2.common.models.mongomodels.UserModel;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

//
public class GroupControllerTests {
    private GroupController groupController;
    private DatabaseService<Group> groupService;
    private DatabaseService<UserModel> userService;
    private DatabaseService<AuthenticationData> authService;
    private String groupName;
    private Group group;
    private Group group1;
    private UserModel user;
    private UserModel user1;
    private UserModel user2;
    private UserModel moderatorUser;
    private AuthenticationData authGroup;
    //  private UserModel invitedUser;
    private String token;
    private String token1;
    private String token2;
    private String garbageToken;

    private String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Before
    public void setUp() {
        groupController = new GroupController();
        groupService = GroupService.getInstance();
        userService = UserService.getInstance();
        groupName = "Demo Group" + getRandomString();
        garbageToken = "Bearer garbageToken";

        user = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();
        user1 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();
        user2 = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Demo User" + getRandomString())
                .setGroups(Collections.emptyList())
                .build();
        group = Group.groupBuilder()
                .setId(new ObjectId())
                .setName("group name" + getRandomString())
                .setUsers(Collections.emptyList())
                .setCurrentInvitedUsers(Collections.emptyList())
                .build();
        group1 = Group.groupBuilder()
                .setId(new ObjectId())
                .setName("group name" + getRandomString())
                .setUsers(Collections.emptyList())
                .setCurrentInvitedUsers(Collections.emptyList())
                .build();

        moderatorUser = UserModel.userBuilder()
                .setId(new ObjectId())
                .setUsername("Moderator User" + getRandomString())
                .build();

//    invitedUser = UserModel.userBuilder()
//        .setId(new ObjectId())
//        .setUsername("Invited User")
//        .build();
    }

    private String getAuthTokenFromUserId(String userId) {
        return "Bearer " + JWTUtils.generateJWToken(userId);
    }

    @Test
    public void basicCreateGroupTest() {
        userService.add(user);
        Response response = groupController.createGroup(groupName, garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        token = getAuthTokenFromUserId(user.getId().toString());
        response = groupController.createGroup(groupName, token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, groupName)).next();
        assertEquals(groupName, foundGroup.getName());

        //db clean up
        groupService.deleteById(foundGroup.getId());
        userService.deleteById(user.getId());
    }

    @Test
    public void createGroupWithNoSuchUser() {
        token = getAuthTokenFromUserId(new ObjectId().toString());
        Response response = groupController.createGroup(groupName, token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
    }

    //Sucessfully deleted - 200 Ok
    @Test
    public void deleteGroup() {
        // create a List of moderators, which has the above created user
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(user.getId());
        // create an empty list of users
        List<ObjectId> usersInGroup = new ArrayList<>();
        //setting users and moderators in group
        group.setName(groupName);
        group.setUsers(usersInGroup);
        group.setModerators(moderators);
        //add group to db
        userService.add(user);
        groupService.add(group);

        token = getAuthTokenFromUserId(user.getId().toString());
        Response response = groupController.deleteGroup(group.getId().toString(), garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        response = groupController.deleteGroup(group.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        //clean up DB
        groupService.deleteById(group.getId());
        userService.deleteById(user.getId());
    }

    //Cannot delete, because user not moderator
    @Test
    public void cannotDeleteGroup() {
        // create a List of moderators, which has the above created user
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(new ObjectId());
        // create an empty list of users
        List<ObjectId> usersInGroup = new ArrayList<>();
        //adding users and moderators to group
        group.setName(groupName);
        group.setUsers(usersInGroup);
        group.setModerators(moderators);
        //adding group to DB
        userService.add(user);
        groupService.add(group);

        token = getAuthTokenFromUserId(user.getId().toString());
        Response response = groupController.deleteGroup(group.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        //no such group to delete
        response = groupController.deleteGroup(new ObjectId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        //clean up DB
        groupService.deleteById(group.getId());
        userService.deleteById(user.getId());
    }

    // search group found - 200 ok
    @Test
    public void searchGroup() {
        //setting group name
        group.setName(groupName);
        //adding group to DB
        groupService.add(group);

        Response response = groupController.searchGroup(groupName);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        //clean up DB
        groupService.deleteById(group.getId());
        userService.deleteById(user.getId());
    }


    // search group not found - Forbidden
    @Test
    public void searchGroupNotFound() {
        // setting name of the group and adding it to DB
        group.setName(groupName);
        groupService.add(group);

        Response response = groupController.searchGroup("randomString");
        assertEquals("[]", response.getEntity());
        assertEquals(Response.Status.OK, response.getStatusInfo());

        // clean up DB
        groupService.deleteById(group.getId());
        userService.deleteById(user.getId());
    }

    // basic add user test
    @Test
    public void basicAddUserTest() {
        //add user and mocerator to DB
        userService.add(moderatorUser);
        userService.add(user);
        // add moderator to a list
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        List<ObjectId> invitedUsers = new ArrayList<>();
        invitedUsers.add(user.getId());
        // set group name and moderators in the group and add it to DB
        group.setName(groupName);
        group.setModerators(moderators);
        group.setCurrentInvitedUsers(invitedUsers);
        groupService.add(group);

        //no such moderator
        Response response = groupController.addUserToGroup(group.getId().toString(), user.getId().toString(), garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        //not a moderator
        token = getAuthTokenFromUserId(new ObjectId().toString());
        response = groupController.addUserToGroup(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        //token for the moderator which we would pass while adding a user
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        response = groupController.addUserToGroup(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        // fetch the updated group from DB again
        group = groupService.findById(group.getId());
        List<ObjectId> listOfUsers = group.getUsers();
        assertTrue(listOfUsers.contains(user.getId()));

        //fetch the updated user from DB again
        user = userService.findById(user.getId());
        List<ObjectId> listOfGroups = user.getGroups();
        assertTrue(listOfGroups.contains(group.getId()));

        //clean up the DB
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    //user already in the group test
    @Test
    public void userAlreadyPresentTest() {
        //adding user and moderator to DB
        userService.add(moderatorUser);
        userService.add(user);
        // creating a list of moderators and users
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());
        // setting those in the group
        group.setName(groupName);
        group.setUsers(users);
        group.setModerators(moderators);
        // adding a group to DB
        groupService.add(group);
        // auth token for moderator
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());

        Response response = groupController.addUserToGroup(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        // cleaning up DB
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    // no such group/user present
    @Test
    public void noSuchGroupOrUserToAddTest() {
        token = getAuthTokenFromUserId(new ObjectId().toString());
        Response response = groupController.addUserToGroup(new ObjectId().toString(), new ObjectId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
    }


    @Test
    public void removeUserFromGroupTest() {
        // adding user and moderator to DB
        userService.add(user);
        userService.add(moderatorUser);
        // adding moderator and user to a list
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());

        // setting those in the group
        group.setName(groupName);
        group.setUsers(users);
        group.setModerators(moderators);
        // adding a group to DB
        groupService.add(group);
        //no such moderator
        Response response = groupController.removeUserFromGroup(group.getId().toString(), user.getId().toString(), garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        //not a moderator
        token = getAuthTokenFromUserId(new ObjectId().toString());
        response = groupController.removeUserFromGroup(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        // auth token for moderator
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        response = groupController.removeUserFromGroup(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        // cleaning up the DB
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    // no such user to remove
    @Test
    public void noSuchUserToRemoveTest() {
        userService.add(moderatorUser);
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());

        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());

        group.setName(groupName);
        group.setUsers(users);
        group.setModerators(moderators);
        groupService.add(group);

        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        Response response = groupController.removeUserFromGroup(group.getId().toString(), new ObjectId().toString(), token);
        userService.deleteById(moderatorUser.getId());
        userService.deleteById(user.getId());
        groupService.deleteById(group.getId());
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
    }

    // different user to remove
    @Test
    public void differentUserToRemoveTest() {
        // adding user and moderator to DB
        userService.add(user);
        userService.add(moderatorUser);
        // adding moderator and user to a list
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());
        // setting those in the group
        group.setName(groupName);
        group.setUsers(users);
        group.setModerators(moderators);
        // adding a group to DB
        groupService.add(group);
        // auth token for moderator
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        Response response = groupController.removeUserFromGroup(group.getId().toString(), new ObjectId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
        // cleaning up the DB
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    // assign the role of moderator to a user for group
    @Test
    public void assignModeratorTest() {
        // adding user and moderator to DB
        userService.add(user);
        userService.add(moderatorUser);
        // adding moderator and user to a list
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());
        // setting those in the group
        group.setName(groupName);
        group.setUsers(users);
        group.setModerators(moderators);
        // adding a group to DB
        groupService.add(group);
        //no such moderator
        Response response = groupController.assignModerator(group.getId().toString(), user.getId().toString(), garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        //not a moderator
        token = getAuthTokenFromUserId(new ObjectId().toString());
        response = groupController.assignModerator(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        // auth token for moderator
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        response = groupController.assignModerator(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        group = groupService.findById(group.getId());
        moderators = group.getModerators();
        assertTrue(moderators.contains(user.getId()));

        // cleaning up the DB
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    @Test
    public void userAlreadyModeratorTest() {
        //adding user and moderator to DB
        userService.add(moderatorUser);
        userService.add(user);
        // adding users and moderators
        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        moderators.add(user.getId());
        List<ObjectId> users = new ArrayList<>();
        users.add(user.getId());
        users.add(moderatorUser.getId());
        // setting them in group
        group.setName(groupName);
        group.setUsers(users);
        group.setModerators(moderators);
        groupService.add(group);
        // auth token for moderator
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        Response response = groupController.assignModerator(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
        group = groupService.findById(group.getId());
        List<ObjectId> foundModerators = group.getModerators();
        assertTrue(foundModerators.contains(user.getId()));

        //cleaning up the DB
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    // no such group or user
    @Test
    public void noSuchGroupToAssignTest() {
        userService.add(user);
        token = getAuthTokenFromUserId(user.getId().toString());
        Response response = groupController.assignModerator(new ObjectId().toString(), user.getId().toString(), token);

        userService.deleteById(user.getId());
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
    }

    @Test
    public void inviteUsersToGroupTest() {
        Response response = groupController.inviteUserToGroup(new ObjectId().toString(), new ObjectId().toString());
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        List<ObjectId> invitedUsersList = new ArrayList<>();
        invitedUsersList.add(user.getId());


        group.setName(groupName);
        group.setCurrentInvitedUsers(invitedUsersList);

        groupService.add(group);
        userService.add(user);
        //user already in invite list
        response = groupController.inviteUserToGroup(user.getId().toString(), group.getId().toString());
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
        //removing the user from invitelist
        invitedUsersList.remove(user.getId());
        groupService.deleteById(group.getId());

        group.setCurrentInvitedUsers(invitedUsersList);
        groupService.add(group);
        response = groupController.inviteUserToGroup(user.getId().toString(), group.getId().toString());
        assertEquals(Response.Status.OK, response.getStatusInfo());
        //cleaning up the DB
        userService.deleteById(user.getId());
        groupService.deleteById(group.getId());
    }

    @Test
    public void testCurrentInvitedUsers() {
        Response response = groupController.getCurrentInvitedUsers(new ObjectId().toString(), garbageToken);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(user.getId());
        group.setName(groupName);
        group.setModerators(moderators);
        groupService.add(group);

        // no such moderator
        response = groupController.getCurrentInvitedUsers(group.getId().toString(), garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        // not a moderator
        token = getAuthTokenFromUserId(new ObjectId().toString());
        response = groupController.getCurrentInvitedUsers(group.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        token = getAuthTokenFromUserId(user.getId().toString());
        response = groupController.getCurrentInvitedUsers(group.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        //clean up the DB
        groupService.deleteById(group.getId());
    }

    @Test
    public void rejectInvitedUserTest() {
        Response response = groupController.rejectInvitedUser(new ObjectId().toString(), new ObjectId().toString(), garbageToken);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());

        group.setName(groupName);
        group.setModerators(moderators);

        userService.add(user);
        userService.add(moderatorUser);
        groupService.add(group);

        // garbage token
        response = groupController.rejectInvitedUser(group.getId().toString(), user.getId().toString(), garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        // not a moderator
        token = getAuthTokenFromUserId(new ObjectId().toString());
        response = groupController.rejectInvitedUser(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());
        // Actual moderator
        // user not present in invite list
        token = getAuthTokenFromUserId(moderatorUser.getId().toString());
        response = groupController.rejectInvitedUser(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());
        groupService.deleteById(group.getId());

        List<ObjectId> invitedUsers = new ArrayList<>();
        invitedUsers.add(user.getId());
        group.setCurrentInvitedUsers(invitedUsers);
        groupService.add(group);

        response = groupController.rejectInvitedUser(group.getId().toString(), user.getId().toString(), token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        group = groupService.findById(group.getId());
        assertTrue(!group.getCurrentInvitedUsers().contains(user.getId()));

        //cleaning up the DP
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    @Test
    public void testAllUsersInGroup() {

        userService.add(user);
        userService.add(moderatorUser);

        List<ObjectId> userIds = new ArrayList<>();
        userIds.add(user.getId());
        userIds.add(moderatorUser.getId());
        group.setUsers(userIds);

        groupService.add(group);

        Response response = groupController.getAllUsersOfGroup(group.getId().toString());
        assertEquals(Response.Status.OK, response.getStatusInfo());

        // cleaning up the data
        userService.deleteById(user.getId());
        userService.deleteById(moderatorUser.getId());
        groupService.deleteById(group.getId());
    }

    @Test
    public void createSecureGroupTest() {
        userService.add(user);
        UserCredentialsParams credentials = new UserCredentialsParams();
        credentials.setGroupname(groupName);
        credentials.setPassword("g1");
        Response response = groupController.createSecureGroup(credentials, garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        token = getAuthTokenFromUserId(user.getId().toString());
        response = groupController.createSecureGroup(credentials, token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        token1 = getAuthTokenFromUserId(user1.getId().toString());
        Response response1 = groupController.createSecureGroup(credentials, token1);
        assertEquals(Response.Status.FORBIDDEN, response1.getStatusInfo());

        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, groupName)).next();
        assertEquals(groupName, foundGroup.getName());

        //db clean up
        groupService.deleteById(foundGroup.getId());
        userService.deleteById(user.getId());
        userService.deleteById(user1.getId());
    }

    @Test
    public void groupAlreadyPresentTest() {
        userService.add(user);
        UserCredentialsParams credentials = new UserCredentialsParams();
        credentials.setGroupname(groupName);
        credentials.setPassword("g1");

        token = getAuthTokenFromUserId(user.getId().toString());
        Response response = groupController.createSecureGroup(credentials, token);
        assertEquals(Response.Status.OK, response.getStatusInfo());
        try {
            response = groupController.createSecureGroup(credentials, token);
        }catch (MongoWriteException e){
            assertEquals(Response.Status.OK, response.getStatusInfo());
        }
        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, groupName)).next();
        assertEquals(groupName, foundGroup.getName());

        userService.deleteById(user.getId());
        groupService.deleteById(foundGroup.getId());
    }

    @Test
    public void accessSecureGroupTest() {
        userService.add(user);
        userService.add(user2);
        token = getAuthTokenFromUserId(user.getId().toString());

        UserCredentialsParams credentials = new UserCredentialsParams();
        credentials.setGroupname(groupName);
        credentials.setPassword("g1");

        groupController.createSecureGroup(credentials, token);

        Response response = groupController.accessGroup(credentials, garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        response = groupController.accessGroup(credentials, token);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        token = getAuthTokenFromUserId(user2.getId().toString());
        response = groupController.accessGroup(credentials, token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        token1 = getAuthTokenFromUserId(user1.getId().toString());
        token2 = getAuthTokenFromUserId(group1.getId().toString());
        Response response1 = groupController.accessGroup(credentials, token1);
        Response response2 = groupController.accessGroup(credentials, token2);
        assertEquals(Response.Status.FORBIDDEN, response1.getStatusInfo());
        assertEquals(Response.Status.FORBIDDEN, response2.getStatusInfo());

        credentials.setPassword("g2");
        response = groupController.accessGroup(credentials, token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, groupName)).next();
        assertEquals(groupName, foundGroup.getName());

        //db clean up
        groupService.deleteById(foundGroup.getId());
        userService.deleteById(user.getId());
        userService.deleteById(user1.getId());
    }

    @Test
    public void setGroupPasswordTest() {

        userService.add(moderatorUser);
        userService.add(user2);

        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        System.out.println(moderatorUser.getId().toString());

        group.setModerators(moderators);
        groupService.add(group);

        token1 = getAuthTokenFromUserId(moderatorUser.getId().toString());

        UserCredentialsParams credentials = new UserCredentialsParams();
        credentials.setId(group.getId());
        credentials.setPassword("1234");

        Response response = groupController.setGroupPassword(credentials, garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        response = groupController.setGroupPassword(credentials, token1);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        token = getAuthTokenFromUserId(user2.getId().toString());
        response = groupController.setGroupPassword(credentials, token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        token1 = getAuthTokenFromUserId(user1.getId().toString());
        token2 = getAuthTokenFromUserId(group1.getId().toString());
        Response response1 = groupController.setGroupPassword(credentials, token1);
        Response response2 = groupController.setGroupPassword(credentials, token2);
        assertEquals(Response.Status.FORBIDDEN, response1.getStatusInfo());
        assertEquals(Response.Status.FORBIDDEN, response2.getStatusInfo());

        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, group.getName())).next();
        assertEquals(group.getName(), foundGroup.getName());

        //db clean up
        groupService.deleteById(foundGroup.getId());
        userService.deleteById(moderatorUser.getId());
        userService.deleteById(user2.getId());
    }

    @Test
    public void changeGroupPasswordTest() {

        userService.add(moderatorUser);
        userService.add(user2);

        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        System.out.println(moderatorUser.getId().toString());

        group.setModerators(moderators);
        groupService.add(group);

        UserCredentialsParams credentials = new UserCredentialsParams();
        credentials.setId(group.getId());
        credentials.setPassword("12345");

        token1 = getAuthTokenFromUserId(moderatorUser.getId().toString());
        groupController.setGroupPassword(credentials, token1);

        credentials.setOldPassword("12345");
        credentials.setNewPassword("123");

        Response response = groupController.changePassword(credentials, garbageToken);
        assertEquals(Response.Status.UNAUTHORIZED, response.getStatusInfo());

        token = getAuthTokenFromUserId(user2.getId().toString());
        response = groupController.changePassword(credentials, token);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        response = groupController.changePassword(credentials, token1);
        assertEquals(Response.Status.OK, response.getStatusInfo());

        token1 = getAuthTokenFromUserId(user1.getId().toString());
        token2 = getAuthTokenFromUserId(group1.getId().toString());
        Response response1 = groupController.changePassword(credentials, token1);
        Response response2 = groupController.changePassword(credentials, token2);
        assertEquals(Response.Status.FORBIDDEN, response1.getStatusInfo());
        assertEquals(Response.Status.FORBIDDEN, response2.getStatusInfo());

        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, group.getName())).next();
        assertEquals(group.getName(), foundGroup.getName());

        //db clean up
        groupService.deleteById(foundGroup.getId());
        userService.deleteById(moderatorUser.getId());
        userService.deleteById(user2.getId());
    }

    @Test
    public void changeGroupPasswordIncorrectOldPasswordTest() {

        userService.add(moderatorUser);

        List<ObjectId> moderators = new ArrayList<>();
        moderators.add(moderatorUser.getId());
        System.out.println(moderatorUser.getId().toString());

        group.setModerators(moderators);
        groupService.add(group);

        UserCredentialsParams credentials = new UserCredentialsParams();
        credentials.setId(group.getId());
        credentials.setPassword("1234");

        token1 = getAuthTokenFromUserId(moderatorUser.getId().toString());
        groupController.setGroupPassword(credentials, token1);

        credentials.setOldPassword("123");
        credentials.setNewPassword("12");

        Response response = groupController.changePassword(credentials, token1);
        assertEquals(Response.Status.FORBIDDEN, response.getStatusInfo());

        Group foundGroup = groupService.findBy(Filters.eq(Group.GROUPNAME_FIELD, group.getName())).next();
        assertEquals(group.getName(), foundGroup.getName());

        //db clean up
        groupService.deleteById(foundGroup.getId());
        userService.deleteById(moderatorUser.getId());
        userService.deleteById(user2.getId());
    }

    @Test
    public void fetchAllGroupsTest() {
        userService.add(moderatorUser);
        token1 = getAuthTokenFromUserId(moderatorUser.getId().toString());
        Response response = groupController.fetchAllGroups(token1);

        assertEquals(Response.Status.OK, response.getStatusInfo());

        token1 = garbageToken;
        Response response1 = groupController.fetchAllGroups(token1);
        assertEquals(Response.Status.UNAUTHORIZED, response1.getStatusInfo());

        userService.deleteById(moderatorUser.getId());
    }

}
