package fse.team2.common.models.controllermodels;

public class ProfilePictureParams {

    public static final String DEFAULT_KEYWORD_FOR_NOW = "default";

    String forUser;
    String profilePictureContents;
    String fileName;

    public ProfilePictureParams() {

    }

    public ProfilePictureParams(String forUser, String profilePictureContents, String fileName) {
        this.forUser = forUser;
        this.profilePictureContents = profilePictureContents;
        this.fileName = fileName;
    }

    public String getProfilePictureContents() {
        return profilePictureContents;
    }

    public String getForUser() {
        return forUser;
    }

    public String getFileName() {
        return fileName;
    }
}
