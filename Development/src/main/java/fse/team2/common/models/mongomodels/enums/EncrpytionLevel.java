package fse.team2.common.models.mongomodels.enums;

import com.neu.prattle.service.encryptionservice.BasicEncryption;
import com.neu.prattle.service.encryptionservice.DummyEncryption;
import com.neu.prattle.service.encryptionservice.EncryptionService;

/**
 * Defines the level of encryption of the message.
 */
public enum EncrpytionLevel {
    NONE {
        @Override
        public EncryptionService getEncryptionService() {
            return DummyEncryption.getInstance();
        }
    },
    BASIC {
        @Override
        public EncryptionService getEncryptionService() {
            return BasicEncryption.getInstance();
        }
    };

    public abstract EncryptionService getEncryptionService();
}
