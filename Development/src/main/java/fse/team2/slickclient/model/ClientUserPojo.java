package fse.team2.slickclient.model;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import fse.team2.common.models.mongomodels.enums.PreferenceType;
import fse.team2.common.models.mongomodels.preferences.Preference;

public class ClientUserPojo {
    private String id;
    private String username;
    private String name;
    private boolean deleted;
    private boolean hidden;
    private List<Preference> preferences;
    private List<String> followers;
    private List<String> following;
    private List<String> messages;
    private List<String> groups;
    private String token;

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
        this.preferences = preferences;
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

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        String pictureUrl = "undefined";
        if (preferences != null) {
            Iterator<Preference> preferenceIterator = preferences.iterator();
            Preference profilePicture = null;
            while (preferenceIterator.hasNext()) {
                Preference preference = preferenceIterator.next();
                if (preference.getPreferenceType() == PreferenceType.DEFAULT_PROFILE_PICTURE) {
                    profilePicture = preference;
                    break;
                }
            }

            if (profilePicture != null) {
                pictureUrl = profilePicture.getValue();
            }
        }
        return "ClientUserPojo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                '}' + "Profile Picture: " + pictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientUserPojo that = (ClientUserPojo) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
