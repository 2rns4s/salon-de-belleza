package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.notification;

import com.salondebelleza.salondebelleza.agendamiento.application.event.CitaConfirmadaEvent;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class TwilioSmsNotificationListener {

    private final String accountSid;
    private final String authToken;
    private final String fromPhoneNumber;
    private boolean habilitado = false;

    public TwilioSmsNotificationListener(
            @Value("${twilio.account-sid}") String accountSid,
            @Value("${twilio.auth-token}") String authToken,
            @Value("${twilio.phone-number}") String fromPhoneNumber) {
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromPhoneNumber = fromPhoneNumber;
    }

    @PostConstruct
    public void init() {
        if (accountSid == null || accountSid.isBlank() || accountSid.contains("XXXX") ||
                authToken == null || authToken.isBlank() || authToken.contains("your_auth_token")) {
            log.warn("Twilio no está configurado con credenciales válidas en las variables de entorno. Las notificaciones SMS estarán deshabilitadas.");
            this.habilitado = false;
            return;
        }

        try {
            Twilio.init(accountSid, authToken);
            this.habilitado = true;
            log.info("Twilio inicializado correctamente");
        } catch (Exception e) {
            log.error("Error al inicializar cliente Twilio: {}", e.getMessage());
            this.habilitado = false;
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCitaConfirmadaEvent(CitaConfirmadaEvent event) {
        if (!habilitado) {
            log.info("Notificación SMS omitida para cita ID {}: Twilio no está habilitado o configurado.", event.idCita());
            return;
        }

        try {
            log.info("Enviando SMS de confirmación vía Twilio para cita ID: {}", event.idCita());

            String telefonoDestino = event.telefonoCliente();

            if (!telefonoDestino.startsWith("+57")) {
                telefonoDestino = "+57" + telefonoDestino;
            }

            String cuerpoMensaje = String.format(
                    "Hola %s, tu cita para %s ha sido confirmada para el %s. ¡Te esperamos en el salón de belleza!",
                    event.nombreCliente(),
                    event.nombreServicio(),
                    event.fechaHoraCita()
            );

            Message mensaje = Message.creator(
                    new PhoneNumber(telefonoDestino),
                    new PhoneNumber(fromPhoneNumber),
                    cuerpoMensaje
            ).create();

            log.info("SMS enviado exitosamente para cita ID {}: SID del mensaje {}", event.idCita(), mensaje.getSid());

        } catch (Exception e) {
            log.error("Error al enviar SMS para la cita confirmada con id {}: {}", event.idCita(), e.getMessage());
        }
    }

}
