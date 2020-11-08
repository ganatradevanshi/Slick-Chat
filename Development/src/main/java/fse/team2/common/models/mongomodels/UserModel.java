package fse.team2.common.models.mongomodels;

import com.mongodb.client.model.ValidationOptions;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;

import fse.team2.common.models.mongomodels.enums.UserType;
import fse.team2.common.models.mongomodels.preferences.Preference;
import fse.team2.common.utils.Utils;

/***
 * This class represents a User model.
 */
public class UserModel {
  public static final String COLLECTION_NAME = "users";

  public static final String USERNAME_FIELD = "username";
  public static final String ID_FIELD = "_id";
  public static final String PREFERENCES_FIELD = "preferences";


  private ObjectId id;
  @BsonProperty(USERNAME_FIELD)
  private String username;
  private String name;
  private boolean deleted;
  private boolean hidden;
  private List<Preference> preferences;
  private List<ObjectId> followers;
  private List<ObjectId> following;
  private List<ObjectId> messages;
  private List<ObjectId> groups;
  private String token;
  private boolean isOAuthUser;
  private boolean isTracked;
  private UserType typeOfUser;

  public UserModel() {
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
    return Objects.hash(name);
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
    if (!(obj instanceof UserModel))
      return false;

    UserModel user = (UserModel) obj;
    return user.getUsername().equals(this.username);
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    if (Utils.checkForNull(id)) {
      throw new IllegalArgumentException("id cannot be null");
    }
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    if (Utils.checkForNull(username)) {
      throw new IllegalArgumentException("User name cannot be null");
    }
    this.username = username;
  }

  public void setName(String name) {
    if (Utils.checkForNull(name)) {
      throw new IllegalArgumentException("User's name cannot be null");
    }
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public List<Preference> getPreferences() {
    return preferences;
  }

  public void setPreferences(List<Preference> preferences) {
    if (Utils.checkForNull(preferences)) {
      throw new IllegalArgumentException("preferences cannot be null");
    }
    this.preferences = preferences;
  }

  public List<ObjectId> getFollowers() {
    return followers;
  }

  public void setFollowers(List<ObjectId> followers) {
    if (Utils.checkForNull(followers)) {
      throw new IllegalArgumentException("Followers cannot be null");
    }
    this.followers = followers;
  }

  public List<ObjectId> getFollowing() {
    return following;
  }

  public void setFollowing(List<ObjectId> following) {
    if (Utils.checkForNull(following)) {
      throw new IllegalArgumentException("Following cannot be null");
    }
    this.following = following;
  }


  public List<ObjectId> getMessages() {
    return messages;
  }

  public void setMessages(List<ObjectId> messages) {
    if (Utils.checkForNull(messages)) {
      throw new IllegalArgumentException("Messages cannot be null");
    }
    this.messages = messages;
  }

  public List<ObjectId> getGroups() {
    return groups;
  }

  public void setGroups(List<ObjectId> groups) {
    if (Utils.checkForNull(groups)) {
      throw new IllegalArgumentException("Groups cannot be null");
    }
    this.groups = groups;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public boolean isOAuthUser() {
    return isOAuthUser;
  }

  public void setOAuthUser(boolean oAuthUser) {
    isOAuthUser = oAuthUser;
  }

  public boolean isTracked() {
    return isTracked;
  }

  public void setTracked(boolean tracked) {
    isTracked = tracked;
  }

  public UserType getTypeOfUser() {
    return typeOfUser;
  }

  public void setTypeOfUser(UserType typeOfUser) {
    this.typeOfUser = typeOfUser;
  }

  public static UserBuilder userBuilder() {
    return new UserBuilder();
  }

  @BsonIgnore
  public static ValidationOptions getValidationOptions() {
    return new ValidationOptions();
  }

  /***
   * A Builder helper class to create instances of {@link UserModel}
   */
  public static class UserBuilder {
    /***
     * Invoking the build method will return this User object.
     */
    UserModel user;

    public UserBuilder() {
      user = new UserModel();
      // by default tracking would be false and would be turned on if mentioned
      user.setTracked(false);
      // by default the users would Normal User unless specified in the setUserType
      user.setTypeOfUser(UserType.NORMAL);
    }

    public UserBuilder setName(String name) {
      user.setName(name);
      return this;
    }

    public UserBuilder setUsername(String username) {
      user.setUsername(username);
      return this;
    }

    public UserBuilder setDelete(boolean isDeleted) {
      user.setDeleted(isDeleted);
      return this;
    }

    public UserBuilder setFollowers(List<ObjectId> followers) {
      user.setFollowers(followers);
      return this;
    }

    public UserBuilder setFollowing(List<ObjectId> following) {
      user.setFollowing(following);
      return this;
    }

    public UserBuilder setId(ObjectId id) {
      user.setId(id);
      return this;
    }

    public UserBuilder setPreferences(List<Preference> preferences) {
      user.setPreferences(preferences);
      return this;
    }

    public UserBuilder setHidden(boolean isHidden) {
      user.setHidden(isHidden);
      return this;
    }

    public UserBuilder setGroups(List<ObjectId> groups) {
      user.setGroups(groups);
      return this;
    }

    public UserBuilder setMessages(List<ObjectId> messages) {
      user.setMessages(messages);
      return this;
    }

    public UserBuilder setIsOAuthUser(boolean isOAuth) {
      user.setOAuthUser(isOAuth);
      return this;
    }

    public UserBuilder setTracked(boolean toTrack) {
      user.setTracked(toTrack);
      return this;
    }

    public UserBuilder setUserType(UserType userType) {
      user.setTypeOfUser(userType);
      return this;
    }

    public UserModel build() {
      return user;
    }
  }

}
