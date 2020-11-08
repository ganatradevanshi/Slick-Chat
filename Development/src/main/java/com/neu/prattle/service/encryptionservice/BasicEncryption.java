package com.neu.prattle.service.encryptionservice;

import com.neu.prattle.utils.ConfigUtils;
import fse.team2.slickclient.utils.LoggerService;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;

/**
 * EncryptionService implementation of {@link EncryptionService} using AES encryption.
 *
 */
public class BasicEncryption implements EncryptionService {

    private SecretKeySpec secretKey;
    private byte[] key;
    private static BasicEncryption instance;

    public static final int GCM_IV_LENGTH = 12;
    public static final int GCM_TAG_LENGTH = 16;

    private byte[] iv;

    private BasicEncryption() {
        String passphrase = ConfigUtils.getInstance().getPropertyValue("basic_encryption_key");
        try {
            key = passphrase.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32);
            secretKey = new SecretKeySpec(key, "AES");
            iv = new byte[GCM_IV_LENGTH];
            Arrays.fill(iv, (byte) 1);

        } catch (NoSuchAlgorithmException e) {
            LoggerService.log(Level.SEVERE, "Encrpytion failed: " + e.getMessage());
        }
    }

    public static BasicEncryption getInstance() {
        if (instance == null) {
            instance = new BasicEncryption();
        }
        return instance;
    }

    @Override
    public String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes()));
        } catch (Exception e) {
            LoggerService.log(Level.SEVERE, "Encryption failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String decrypt(String encryptedContents) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedContents.getBytes())));
        } catch (Exception e) {
            LoggerService.log(Level.SEVERE, "Decryption failed: " + e.getMessage());
        }
        return null;
    }
}
