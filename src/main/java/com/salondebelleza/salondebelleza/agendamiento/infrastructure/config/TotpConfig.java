package com.salondebelleza.salondebelleza.agendamiento.infrastructure.config;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TotpConfig {

    @Bean
    public SecretGenerator secretGenerator() {
        return new DefaultSecretGenerator();
    }

    @Bean
    public QrGenerator qrGenerator() {
        return new ZxingPngQrGenerator();
    }

    // 1. Definimos el Generador como Bean
    @Bean
    public CodeGenerator codeGenerator() {
        return new DefaultCodeGenerator();
    }

    @Bean
    public TimeProvider timeProvider() {
        return new SystemTimeProvider();
    }

    // 2. Inyectamos el generador y el proveedor de tiempo en el Verificador
    @Bean
    public CodeVerifier codeVerifier(CodeGenerator codeGenerator, TimeProvider timeProvider) {
        // Spring nos pasa las instancias ya creadas arriba
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);

        // Opcional: Configurar tolerancia (acepta códigos de hace 1 periodo o 1 futuro)
        // Útil si el reloj del usuario no está perfectamente sincronizado
        verifier.setTimePeriod(30);
        verifier.setAllowedTimePeriodDiscrepancy(1);

        return verifier;
    }
}
