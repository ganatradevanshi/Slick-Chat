package com.neu.prattle.utilstests;

import com.neu.prattle.utils.ConfigUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigUtilsTests {

    @Test
    public void getPropertyTest() {
        ConfigUtils utils = ConfigUtils.getInstance();
        assertEquals("local", utils.getPropertyValue("env"));
    }

    @Test(expected = IllegalStateException.class)
    public void inValidConfigFile() {
        ConfigUtils utils = new ConfigUtils("x.txt");
        utils.getPropertyValue("env");
    }
}
