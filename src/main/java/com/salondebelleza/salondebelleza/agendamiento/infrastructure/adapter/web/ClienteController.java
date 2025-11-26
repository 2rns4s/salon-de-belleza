package com.salondebelleza.salondebelleza.agendamiento.infrastructure.adapter.web;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ActualizarClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.ClienteResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.dto.cliente.CrearClienteRequest;
import com.salondebelleza.salondebelleza.agendamiento.application.usecase.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base-path}/clientes")
public class ClienteController {
    private final ActualizarClienteUseCase actualizarClienteUseCase;
    private final BuscarClienteUseCase buscarClienteUseCase;
    private final CrearClienteUseCase crearClienteUseCase;
    private final EliminarClienteUseCase eliminarClienteUseCase;
    private final ListarClienteUseCase listarClienteUseCase;

    @Autowired
    public ClienteController(ActualizarClienteUseCase actualizarClienteUseCase, BuscarClienteUseCase buscarClienteUseCase, CrearClienteUseCase crearClienteUseCase, EliminarClienteUseCase eliminarClienteUseCase, ListarClienteUseCase listarClienteUseCase) {
        this.actualizarClienteUseCase = actualizarClienteUseCase;
        this.buscarClienteUseCase = buscarClienteUseCase;
        this.crearClienteUseCase = crearClienteUseCase;
        this.eliminarClienteUseCase = eliminarClienteUseCase;
        this.listarClienteUseCase = listarClienteUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.status(HttpStatus.OK).body(listarClienteUseCase.executeByAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarClientePorId(@PathVariable long id) {
        return ResponseEntity.status(HttpStatus.OK).body(buscarClienteUseCase.executeById(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarCliente(@RequestParam(required = false) String telefono,
                                           @RequestParam(required = false) String documento,
                                           @RequestParam(required = false) String nombre) {
        if (telefono != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.buscarClienteUseCase.executeByTelefono(telefono));
        } else if (documento != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.buscarClienteUseCase.executeByDocumento(documento));
        } else if (nombre != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.listarClienteUseCase.executeByNombre(nombre));
        } else return ResponseEntity.badRequest().body("Debe ingresar al menos un parametro");
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> agregarCliente(@Valid @RequestBody CrearClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.crearClienteUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(@PathVariable long id, @Valid @RequestBody ActualizarClienteRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(this.actualizarClienteUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable long id){
        this.eliminarClienteUseCase.executeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
