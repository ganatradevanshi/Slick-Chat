package com.neu.prattle.utilstests;

import com.neu.prattle.utils.BCryptUtils;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class BCryptUtilsTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = BCryptUtils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
