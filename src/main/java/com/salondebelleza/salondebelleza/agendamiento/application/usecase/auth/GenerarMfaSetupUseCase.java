package com.salondebelleza.salondebelleza.agendamiento.application.usecase.auth;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.auth.MfaSetupResponse;
import com.salondebelleza.salondebelleza.agendamiento.domain.exception.ErrorAlGenerarMfaQr;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Base64;

@Service
public class GenerarMfaSetupUseCase {

    private final SecretGenerator secretGenerator;
    private final QrGenerator qrGenerator;
    private final StringRedisTemplate redisTemplate;
    private final long mfaSetupTtlMinutes;
    private final String issuerName;

    // Inyección 100% por constructor. Garantiza Inmutabilidad.
    public GenerarMfaSetupUseCase(
            SecretGenerator secretGenerator,
            QrGenerator qrGenerator,
            StringRedisTemplate redisTemplate,
            // Fallback a 15 min si no existe la propiedad
            @Value("${security.mfa.setup.ttl-minutes:15}") long mfaSetupTtlMinutes,
            // Fallback al nombre por defecto
            @Value("${app.name:SalonDeBelleza}") String issuerName) {

        this.secretGenerator = secretGenerator;
        this.qrGenerator = qrGenerator;
        this.redisTemplate = redisTemplate;
        this.mfaSetupTtlMinutes = mfaSetupTtlMinutes;
        this.issuerName = issuerName;
    }

    public MfaSetupResponse execute(String username) {

        String secret = secretGenerator.generate();
        String redisKey = "mfa_setup:" + username;

        // Guardamos en Redis con el TTL parametrizado
        redisTemplate.opsForValue().set(redisKey, secret, Duration.ofMinutes(mfaSetupTtlMinutes));

        QrData qrData = new QrData.Builder()
                .label(username)
                .secret(secret)
                .issuer(issuerName) // Usamos variable, no hardcoded
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        try {
            byte[] qrCodeBytes = qrGenerator.generate(qrData);
            String qrBase64 = Base64.getEncoder().encodeToString(qrCodeBytes);
            String qrImageUrl = "data:image/png;base64," + qrBase64;

            return new MfaSetupResponse(secret, qrImageUrl);
        } catch (QrGenerationException e) {
            throw new ErrorAlGenerarMfaQr("Error generando QR para MFA");
        }
    }
}
