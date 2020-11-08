package fse.team2.common.models;

import fse.team2.common.models.mongomodels.enums.PreferenceType;
import fse.team2.common.models.mongomodels.preferences.DefaultProfilePicture;
import fse.team2.common.models.mongomodels.preferences.Preference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultProfilePictureTests {

    @Test
    public void defaultProfilePictureTest(){
        Preference preference = new DefaultProfilePicture();
        String profilePictureValue = "http://image.com";
        preference.setValue(profilePictureValue);

        assertEquals(preference.getPreferenceType(), PreferenceType.DEFAULT_PROFILE_PICTURE);
        assertEquals(preference.getValue(), profilePictureValue);
    }
}
