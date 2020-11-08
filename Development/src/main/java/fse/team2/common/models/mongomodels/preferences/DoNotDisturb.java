package fse.team2.common.models.mongomodels.preferences;

import fse.team2.common.models.mongomodels.enums.PreferenceType;

/**
 * This class represents a DoNotDisturb preference of a user.
 */
public class DoNotDisturb extends Preference {

    /**
     * Create a do not disturb preference with default values.
     */
    public DoNotDisturb() {
        setPreferenceType(PreferenceType.DND);
        setValue("false");
    }

    /**
     * Create a do not disturb preference with provided values.
     */
    public DoNotDisturb(Boolean value) {
        setPreferenceType(PreferenceType.DND);
        setValue(value.toString());
    }

    /**
     * Create a do not disturb preference with provided values.
     */
    public DoNotDisturb(String value) {
        setPreferenceType(PreferenceType.DND);
        setValue(value);
    }

}
