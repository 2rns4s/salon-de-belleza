package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
@Component
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static byte[] KEY;

    @Value("${security.db.encryption.key}")
    public void setKey(String key) {
        KEY = key.getBytes();
    }

    @Override
    public String convertToDatabaseColumn(String datoEnTextoPlano) {
        if (datoEnTextoPlano == null) {
            return null;
        };
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"));
            return Base64.getEncoder().encodeToString(cipher.doFinal(datoEnTextoPlano.getBytes()));
        }catch (Exception e){
            throw new RuntimeException("Error al encriptar el dato: " + e.getMessage());
        }
    }

    @Override
    public String convertToEntityAttribute(String datoEnTextoCifrado) {
        if (datoEnTextoCifrado == null) {
            return null;
        };
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"));
            return new String(cipher.doFinal(Base64.getDecoder().decode(datoEnTextoCifrado)));
        }catch (Exception e){
            throw new RuntimeException("Error al desencriptar el dato: " + e.getMessage());
        }
    }
}
