package fse.team2.common.models;

import fse.team2.common.models.mongomodels.preferences.DoNotDisturb;
import fse.team2.common.models.mongomodels.preferences.Preference;
import fse.team2.common.models.mongomodels.enums.PreferenceType;
import org.junit.Test;

import static org.junit.Assert.*;

public class PreferenceTests {

    private Preference preference;

    @Test
    public void testDoNotDisturb1() {
        preference = new DoNotDisturb();
        assertFalse(Boolean.parseBoolean(preference.getValue()));
        assertEquals(PreferenceType.DND, preference.getPreferenceType());
    }

    @Test
    public void testDoNotDisturb2() {
        preference = new DoNotDisturb(true);
        assertTrue(Boolean.parseBoolean(preference.getValue()));
        assertEquals(PreferenceType.DND, preference.getPreferenceType());
    }

    @Test
    public void testDoNotDisturb3() {
        preference = new DoNotDisturb(false);
        assertFalse(Boolean.parseBoolean(preference.getValue()));
        assertEquals(PreferenceType.DND, preference.getPreferenceType());
    }

    @Test
    public void testDoNotDisturbStringConstructor1() {
        preference = new DoNotDisturb("true");
        assertTrue(Boolean.parseBoolean(preference.getValue()));
        assertEquals(PreferenceType.DND, preference.getPreferenceType());
    }

    @Test
    public void testDoNotDisturbStringConstructor2() {
        preference = new DoNotDisturb("false");
        assertFalse(Boolean.parseBoolean(preference.getValue()));
        assertEquals(PreferenceType.DND, preference.getPreferenceType());
    }
}
