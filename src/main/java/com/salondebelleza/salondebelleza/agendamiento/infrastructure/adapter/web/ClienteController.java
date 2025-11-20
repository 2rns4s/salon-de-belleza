package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.usecase.CrearClienteUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base-path}/clientes")
public class ClienteController {
    private final CrearClienteUseCase crearClienteUseCase;

    @Autowired
    public ClienteController(CrearClienteUseCase crearClienteUseCase) {
        this.crearClienteUseCase = crearClienteUseCase;
    }


}
