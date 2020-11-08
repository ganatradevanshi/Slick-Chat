package fse.team2.common.models.mongomodels;

import com.mongodb.client.model.ValidationOptions;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import fse.team2.common.models.mongomodels.preferences.Preference;
import fse.team2.common.utils.Utils;

public class Group {
  /**
   * This class represents a Group model.
   */

  public static final String COLLECTION_NAME = "groups";
  public static final String GROUPNAME_FIELD = "username";
  public static final String ID_FIELD = "_id";
  private ObjectId id;
  @BsonProperty(GROUPNAME_FIELD)
  private String name;
  private List<Preference> preferences;
  private List<ObjectId> moderators;
  private List<ObjectId> users;
  private List<ObjectId> messages;
  private List<ObjectId> currentInvitedUsers;
  private boolean isSecure;

  public Group() {
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
    if (!(obj instanceof Group))
      return false;

    Group group = (Group) obj;
    return group.getName().equals(this.name);
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    if (Utils.checkForNull(id)) {
      throw new IllegalArgumentException("Group's id cannot be null");
    }
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (Utils.checkForNull(name)) {
      throw new IllegalArgumentException("Group's name cannot be null");
    }
    this.name = name;
  }

  public List<Preference> getPreferences() {
    return preferences;
  }

  public void setPreferences(List<Preference> preferences) {
    if (Utils.checkForNull(preferences)) {
      throw new IllegalArgumentException("Preferences cannot be null");
    }
    this.preferences = preferences;
  }

  public List<ObjectId> getModerators() {
    return moderators;
  }

  public void setModerators(List<ObjectId> moderators) {
    if (Utils.checkForNull(moderators)) {
      throw new IllegalArgumentException("Moderators cannot be null");
    }
    this.moderators = moderators;
  }

  public List<ObjectId> getUsers() {
    return users;
  }

  public void setUsers(List<ObjectId> users) {
    if (Utils.checkForNull(users)) {
      throw new IllegalArgumentException("Users cannot be null");
    }
    this.users = users;
  }


  public List<ObjectId> getCurrentInvitedUsers() {
    return currentInvitedUsers;
  }

  public void setCurrentInvitedUsers(List<ObjectId> currentInvitedUsers) {
    this.currentInvitedUsers = currentInvitedUsers;
  }

  public List<ObjectId> getMessages() {
    return messages;
  }

  public void setMessages(List<ObjectId> messages) {
    this.messages = messages;
  }

  public static GroupBuilder groupBuilder() {
    return new GroupBuilder();
  }

  public boolean getIsSecure() {
    return isSecure;
  }

  public void setIsSecure(boolean secure) {
    isSecure = secure;
  }

  @BsonIgnore
  public static ValidationOptions getValidationOptions() {
    return new ValidationOptions();
  }

  /***
   * A Builder helper class to create instances of {@link UserModel}
   */
  public static class GroupBuilder {
    /***
     * Invoking the build method will return this Group object.
     */
    Group group;

    public GroupBuilder() {
      group = new Group();
      group.setName("");
      group.setModerators(Collections.emptyList());
      group.setUsers(Collections.emptyList());
      group.setPreferences(Collections.emptyList());
      group.setMessages(Collections.emptyList());
    }

    public GroupBuilder setId(ObjectId id) {
      group.setId(id);
      return this;
    }

    public GroupBuilder setName(String name) {
      group.setName(name);
      return this;
    }

    public GroupBuilder setPreferences(List<Preference> preferences) {
      group.setPreferences(preferences);
      return this;
    }

    public GroupBuilder setModerator(List<ObjectId> moderators) {
      group.setModerators(moderators);
      return this;
    }

    public GroupBuilder setUsers(List<ObjectId> users) {
      group.setUsers(users);
      return this;
    }

    public GroupBuilder setMessages(List<ObjectId> messages) {
      group.setMessages(messages);
      return this;
    }

    public GroupBuilder setCurrentInvitedUsers(List<ObjectId> currentInvitedUsers) {
      group.setCurrentInvitedUsers(currentInvitedUsers);
      return this;
    }

    public  GroupBuilder setIsSecure(boolean isSecure){
      group.setIsSecure(isSecure);
      return this;
    }

    public Group build() {
      return group;
    }
  }
}
