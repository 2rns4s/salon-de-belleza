package com.salondebelleza.salondebelleza.agendamiento.domain.port;

import com.salondebelleza.salondebelleza.agendamiento.application.event.CitaConfirmadaEvent;

public interface NotificacionEventPort {
    void enviarCitaConfirmada(CitaConfirmadaEvent citaConfirmadaEvent);
}
