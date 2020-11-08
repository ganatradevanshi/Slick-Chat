package com.neu.prattle.service.encryptionservice;

/**
 * This class doesn't encrypt the contents.
 */
public class DummyEncryption implements EncryptionService {

    public static DummyEncryption getInstance() {
        return new DummyEncryption();
    }

    @Override
    public String encrypt(String content) {
        return content;
    }

    @Override
    public String decrypt(String encryptedContents) {
        return encryptedContents;
    }
}
