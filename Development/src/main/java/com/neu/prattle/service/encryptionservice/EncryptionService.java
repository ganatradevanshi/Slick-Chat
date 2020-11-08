package com.neu.prattle.service.encryptionservice;

/**
 * This interface represents a EncryptionService responsible for encrypting decrypting database writes.
 */
public interface EncryptionService {

    /**
     * Encrypt the content provided.
     *
     * @param content - content to be encrypted
     * @return - encrypted contents
     */
    String encrypt(String content);

    /**
     * Decrypt the provided encrypted content.
     *
     * @param encryptedContents - content to be decrypted.
     * @return - decrypted contents.
     */
    String decrypt(String encryptedContents);
}
