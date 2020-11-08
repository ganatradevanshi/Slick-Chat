package fse.team2.common.models.controllermodels;

import java.util.List;

public class UserInformation {
  private String id;
  private String username;
  private String name;
  private List<String> followers;
  private List<String> following;
  private List<String> groups;
  private String isTracked;

  public UserInformation() {
    // This public constructor is needed by the mongoDB driver to map documents to user POJO.
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getFollowers() {
    return followers;
  }

  public void setFollowers(List<String> followers) {
    this.followers = followers;
  }

  public List<String> getFollowing() {
    return following;
  }

  public void setFollowing(List<String> following) {
    this.following = following;
  }

  public List<String> getGroups() {
    return groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public String getIsTracked() {
    return isTracked;
  }

  public void setIsTracked(String isTracked) {
    this.isTracked = isTracked;
  }
}
