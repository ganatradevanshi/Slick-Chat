package fse.team2.common.models.controllermodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.bson.types.ObjectId;

/**
 * This is the inner class which we would use to decode the payload from JSON body for signup and
 * login functions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredentialsParams {
  private ObjectId id;
  private String username;
  private String groupname;
  private String password;
  private String name;
  private String oAuth;
  private String oldPassword;
  private String newPassword;

  public UserCredentialsParams() {
    // This public constructor is needed by the mongoDB driver to map documents to user POJO.
    this.id = new ObjectId();
  }

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGroupname() {
    return groupname;
  }

  public void setGroupname(String groupname) {
    this.groupname = groupname;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getOAuth() {
    return oAuth;
  }

  public void setOAuth(String oAuth) {
    this.oAuth = oAuth;
  }
}
