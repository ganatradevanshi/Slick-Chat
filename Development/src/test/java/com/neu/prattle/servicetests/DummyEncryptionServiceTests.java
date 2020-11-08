package com.neu.prattle.servicetests;

import com.neu.prattle.service.encryptionservice.DummyEncryption;
import com.neu.prattle.service.encryptionservice.EncryptionService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DummyEncryptionServiceTests {

    @Test
    public void testEncryptDecrypt() {
        EncryptionService service = DummyEncryption.getInstance();
        String contents = "abc";
        assertEquals(contents, service.encrypt(contents));
        assertEquals(contents, service.decrypt(contents));
    }
}
