package com.salondebelleza.salondebelleza.agendamiento.application.usecase.cita;

import com.salondebelleza.salondebelleza.agendamiento.application.dto.cita.CitaResponse;
import com.salondebelleza.salondebelleza.agendamiento.application.mapper.CitaDTOMapper;
import com.salondebelleza.salondebelleza.agendamiento.domain.port.CitaRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarCitaUseCase {

    private final CitaRepositoryPort citaRepositoryPort;
    private final CitaDTOMapper citaDTOMapper;

    @Autowired
    public ListarCitaUseCase (CitaRepositoryPort citaRepositoryPort, CitaDTOMapper citaDTOMapper){
        this.citaRepositoryPort = citaRepositoryPort;
        this.citaDTOMapper = citaDTOMapper;
    }

    public List<CitaResponse> execute(){
        return this.citaDTOMapper.toResponseList(this.citaRepositoryPort.findAll());
    }

}
