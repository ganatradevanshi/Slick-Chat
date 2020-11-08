package com.neu.prattle.servicetests;

import com.neu.prattle.service.encryptionservice.BasicEncryption;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BasicEncryptionServiceTests {

    @Test
    public void testEncryptDecrypt() {
        BasicEncryption basicEncryption = BasicEncryption.getInstance();
        String contents = "contents";
        assertEquals(contents, basicEncryption.decrypt(basicEncryption.encrypt(contents)));
    }

    @Test
    public void testEncryptDecrypt2() {
        BasicEncryption basicEncryption = BasicEncryption.getInstance();
        String contents = "hi";
        assertEquals(contents, basicEncryption.decrypt(basicEncryption.encrypt(contents)));
    }

    @Test
    public void testEncryptDecrypt3() {
        BasicEncryption basicEncryption = BasicEncryption.getInstance();
        String contents = "hello";
        assertEquals(contents, basicEncryption.decrypt(basicEncryption.encrypt(contents)));
    }

    @Test
    public void testNull() {
        BasicEncryption basicEncryption = BasicEncryption.getInstance();
        String contents = null;
        assertEquals(contents, basicEncryption.decrypt(basicEncryption.encrypt(contents)));
    }

    @Test
    public void testSingleton() {
        assertEquals(BasicEncryption.getInstance(), BasicEncryption.getInstance());
    }
}
