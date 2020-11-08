package fse.team2.slickclient.model;

import java.util.List;
import java.util.Objects;

import fse.team2.common.models.mongomodels.preferences.Preference;

public class ClientGroupPojo {
  private String id;
  private String name;
  private List<Preference> preferences;
  private List<String> moderators;
  private List<String> users;
  private List<String> messages;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Preference> getPreferences() {
    return preferences;
  }

  public void setPreferences(List<Preference> preferences) {
    this.preferences = preferences;
  }

  public List<String> getModerators() {
    return moderators;
  }

  public void setModerators(List<String> moderators) {
    this.moderators = moderators;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public List<String> getMessages() {
    return messages;
  }

  public void setMessages(List<String> messages) {
    this.messages = messages;
  }

  @Override
  public String toString() {
    return "ClientGroupPojo{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientGroupPojo that = (ClientGroupPojo) o;
    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}
