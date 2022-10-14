package org.schar.cybersecurity.common;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class Encryption {

    private final PrivateKey privateKey;

    private final String algorithm = "RSA";

    public Encryption(PrivateKey privateKeyFile) {
        this.privateKey = privateKeyFile;
    }

    public String encrypt(String message, PublicKey receiverKey) throws Exception {
        Cipher encryptCipher = Cipher.getInstance(algorithm);
        encryptCipher.init(Cipher.ENCRYPT_MODE, receiverKey);

        return Base64.getEncoder().encodeToString(encryptCipher.doFinal(message.getBytes()));
    }


    public String decrypt(String message) throws Exception {
        Cipher decryptCipher = Cipher.getInstance(algorithm);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(message));
        return new String(decryptedMessageBytes, StandardCharsets.UTF_8);
    }

    public static PublicKey getPublicKey(byte[] keyBytes) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        return keyFactory.generatePublic(keySpec);
    }

}
