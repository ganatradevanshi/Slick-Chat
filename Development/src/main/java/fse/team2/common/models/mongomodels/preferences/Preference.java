package fse.team2.common.models.mongomodels.preferences;

import fse.team2.common.models.mongomodels.enums.PreferenceType;

public class Preference {
    private PreferenceType preferenceType;
    private String value;

    public PreferenceType getPreferenceType() {
        return preferenceType;
    }

    public void setPreferenceType(PreferenceType preferenceType) {
        this.preferenceType = preferenceType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
