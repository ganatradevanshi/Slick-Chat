package fse.team2.slickclient.websockets;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;

public class ClientCommandHandlerTests {

    @Test
    public void testPrivateConstructor() throws Exception {
        Constructor constructor = ClientCommandHandler.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
