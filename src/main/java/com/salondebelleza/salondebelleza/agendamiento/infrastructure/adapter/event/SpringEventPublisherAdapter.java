package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.event;


import com.salondebelleza.salondebelleza.agendamiento.application.event.CitaConfirmadaEvent;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.NotificacionEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringEventPublisherAdapter implements NotificacionEventPort {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void enviarCitaConfirmada(CitaConfirmadaEvent citaConfirmadaEvent) {
        applicationEventPublisher.publishEvent(citaConfirmadaEvent);
    }
}
