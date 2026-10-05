package com.github.edusants22.cs_player_api.jogador;


import com.github.edusants22.cs_player_api.jogador.dto.CriarJogadorDTO;
import com.github.edusants22.cs_player_api.jogador.dto.JogadorResumoDTO;
import com.github.edusants22.cs_player_api.jogador.models.Jogador;
import com.github.edusants22.cs_player_api.jogador.enums.Funcao;
import com.github.edusants22.cs_player_api.jogador.enums.Pais;
import com.github.edusants22.cs_player_api.time.TimeRepository;
import com.github.edusants22.cs_player_api.time.models.Time;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = ("http://localhost:4200"))
@RestController
@RequestMapping("/jogadores")
public class JogadorController {

    private final JogadorService jogadorService;
    private final TimeRepository timeRepository;

    public JogadorController(JogadorService jogadorService, TimeRepository timeRepository){
        this.jogadorService = jogadorService;
        this.timeRepository = timeRepository;
    }

    @GetMapping("/todos")
    public List<Jogador> listarTodos(){
        return jogadorService.listarTodos();
    }

    @GetMapping("/id")
    public Optional <Jogador> buscarPorId(
            @RequestParam Long Id){
                return  jogadorService.buscarPorId(Id);
    }

    @GetMapping("/pais")
    public List <Jogador> buscarPorPais(
            @RequestParam Pais pais) {
                return jogadorService.buscarPorPais(pais);
    }

    @GetMapping("/listaJogadores")
    public List <Jogador> listaJogadores(
            @RequestParam(required = false) Pais pais,
            @RequestParam(required = false) Long timeId,
            @RequestParam(required = false) Funcao funcao) {
                return jogadorService.listaJogadores(timeId, pais, funcao);
    }

    @GetMapping("/listaJogadoresRating")
    public List <Jogador> listaJogadoresRating(
            @RequestParam(required = false) Pais pais,
            @RequestParam(required = false) Long timeId){
                return jogadorService.listaJogadoresRating(pais, timeId);
    }

    @GetMapping("/listaResumoJogadores")
    public List<JogadorResumoDTO> listaResumoJogadores(){
        return jogadorService.listaResumoJogadores();
    }

    @PostMapping("/criarJogador")
    public Jogador criarJogador(@RequestBody CriarJogadorDTO dto){
        return jogadorService.criarJogador(dto);
    }
}
