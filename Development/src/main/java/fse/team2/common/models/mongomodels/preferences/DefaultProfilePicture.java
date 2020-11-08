package fse.team2.common.models.mongomodels.preferences;

import fse.team2.common.models.mongomodels.enums.PreferenceType;

public class DefaultProfilePicture extends Preference {

    /**
     * Create a DefaultProfilePicture instance with empty url to the profile picture.
     */
    public DefaultProfilePicture() {
        setPreferenceType(PreferenceType.DEFAULT_PROFILE_PICTURE);
        setValue("");
    }

    /**
     * Create a DefaultProfilePicture instance with url to the profile picture.
     */
    public DefaultProfilePicture(String value) {
        setPreferenceType(PreferenceType.DEFAULT_PROFILE_PICTURE);
        setValue(value);
    }
}
