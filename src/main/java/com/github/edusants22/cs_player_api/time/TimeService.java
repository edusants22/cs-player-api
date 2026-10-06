package com.github.edusants22.cs_player_api.time;

import com.github.edusants22.cs_player_api.time.models.Time;
import com.github.edusants22.cs_player_api.time.models.dto.CriarTimeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeService {

    private final TimeRepository timeRepository;

    public TimeService(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }


    // Retorna todos os times.
    // Quem realmente possui a lista é o Repository.
    public List<Time> listaTime() {
        return timeRepository.findAll();
    }

    // Adiciona um novo time no banco de dados.
    // Hoje nossa base é uma ArrayList.
    public Time criarTime(CriarTimeDTO criarTimeDTO) {

        if (timeRepository.existsByNomeIgnoreCase(criarTimeDTO.getNome().trim())) {
            throw new RuntimeException("Já existe um time com esse nome.");
        }

        Time time = new Time();

        time.setNome(criarTimeDTO.getNome());
        return timeRepository.save(time);
    }
}