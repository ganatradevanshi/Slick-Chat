package fse.team2.common.utils;

import com.neu.prattle.utils.BCryptUtils;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilsTests {

    @Test
    public void checkisNullTest1() {
        assertTrue(Utils.checkForNull(null));
    }

    @Test
    public void checkisNullTest2() {
        assertFalse(Utils.checkForNull("1"));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = Utils.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
