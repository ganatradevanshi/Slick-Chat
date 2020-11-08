package fse.team2.common.models.mongomodels;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Objects;

import fse.team2.common.utils.Utils;

public class UserActivityData {
  /**
   * This class represents a UserActivity Model
   */

  public static final String COLLECTION_NAME = "user-activity";
  public static final String USERNAME_FIELD = "username";
  public static final String ID_FIELD = "_id";
  private ObjectId id;
  private UserModel user;
  @BsonProperty(USERNAME_FIELD)
  private String username;
  private String loggedInTime;
  private String loggedOutTime;
  private String ipAddress;

  public UserActivityData() {
    // This public constructor is needed by the mongoDB driver to map documents to user POJO.
  }

  /***
   * Returns the hashCode of this object.
   *
   * As name can be treated as a sort of identifier for
   * this instance, we can use the hashCode of "name"
   * for the complete object.
   *
   * @return hashCode of "this"
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /***
   * Makes comparison between two user accounts.
   *
   * Two user objects are equal if their name are equal ( names are case-sensitive )
   *
   * @param obj Object to compare
   * @return a predicate value for the comparison.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof UserActivityData))
      return false;

    UserActivityData userActivityData = (UserActivityData) obj;
    return userActivityData.getId().equals(this.id);
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    if (Utils.checkForNull(id)) {
      throw new IllegalArgumentException("Id cannot be null");
    }
    this.id = id;
  }

  public UserModel getUser() {
    return user;
  }

  public void setUser(UserModel user) {
    this.user = user;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    if (Utils.checkForNull(username)) {
      throw new IllegalArgumentException("Username cannot be null");
    }
    this.username = username;
  }

  public String getLoggedInTime() {
    return loggedInTime;
  }

  public void setLoggedInTime(String loggedInTime) {
    if (Utils.checkForNull(loggedInTime)) {
      throw new IllegalArgumentException("Logged in time cannot be null");
    }
    this.loggedInTime = loggedInTime;
  }

  public String getLoggedOutTime() {
    return loggedOutTime;
  }

  public void setLoggedOutTime(String loggedOutTime) {
    if (Utils.checkForNull(loggedOutTime)) {
      throw new IllegalArgumentException("Logged out time cannot be null");
    }
    this.loggedOutTime = loggedOutTime;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public static UserActivityBuilder userActivityBuilder() {
    return new UserActivityBuilder();
  }

  public static class UserActivityBuilder {
    UserActivityData userActivityData;

    public UserActivityBuilder() {
      userActivityData = new UserActivityData();
      userActivityData.setUsername("");
    }

    public UserActivityBuilder setId(ObjectId id) {
      userActivityData.setId(id);
      return this;
    }

    public UserActivityBuilder setUser(UserModel user) {
      userActivityData.setUser(user);
      userActivityData.setUsername(user.getUsername());
      return this;
    }

    public UserActivityBuilder setLoggedInTime(String loggedInTime) {
      userActivityData.setLoggedInTime(loggedInTime);
      return this;
    }

    public UserActivityBuilder setLogOutTime(String logOutTime) {
      userActivityData.setLoggedOutTime(logOutTime);
      return this;
    }

    public UserActivityBuilder setIpAddress(String ipAddress) {
      userActivityData.setIpAddress(ipAddress);
      return this;
    }

    public UserActivityData build() {
      return userActivityData;
    }
  }
}
