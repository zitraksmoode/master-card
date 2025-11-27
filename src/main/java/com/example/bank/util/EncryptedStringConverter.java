package com.example.bank.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
@Component
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    @Value("${encryption.key}")
    private String secretKey; // 32 символа = 256 бит

    private byte[] getKey() {
        return secretKey.getBytes();
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[IV_LENGTH];
            new java.security.SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getKey(), "AES"), spec);
            byte[] encrypted = cipher.doFinal(attribute.getBytes());
            byte[] result = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting card number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(dbData);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            byte[] encrypted = new byte[decoded.length - IV_LENGTH];
            System.arraycopy(decoded, IV_LENGTH, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(getKey(), "AES"), spec);
            return new String(cipher.doFinal(encrypted));
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting card number", e);
        }
    }
}