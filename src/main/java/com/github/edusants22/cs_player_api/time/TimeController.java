package com.github.edusants22.cs_player_api.time;


import com.github.edusants22.cs_player_api.jogador.JogadorRepository;
import com.github.edusants22.cs_player_api.jogador.JogadorService;
import com.github.edusants22.cs_player_api.time.models.Time;
import com.github.edusants22.cs_player_api.time.models.dto.CriarTimeDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = ("http://localhost:4200"))
@RestController
@RequestMapping("/times")
public class TimeController {


    private final TimeService timeService;

    public TimeController(TimeService timeService){

        this.timeService = timeService;
    }

    @GetMapping("/todos")
    public List<Time> listarTodos(){
        return timeService.listaTime();
    }

    @PostMapping("/criarTime")
    public Time CriarTime (@RequestBody CriarTimeDTO dto){
        return timeService.criarTime(dto);
    }
}
