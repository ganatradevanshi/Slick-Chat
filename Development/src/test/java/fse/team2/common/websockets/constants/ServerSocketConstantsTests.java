package fse.team2.common.websockets.constants;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ServerSocketConstantsTests {

    @Test
    public void mainSocketURITest() {
        String endpoint = "/main/";
        assertEquals(ServerSocketConstants.MAIN_ENDPOINT_URI, endpoint);
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = ServerSocketConstants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
