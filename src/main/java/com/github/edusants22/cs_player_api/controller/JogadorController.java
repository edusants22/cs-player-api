package com.github.edusants22.cs_player_api.controller;


import com.github.edusants22.cs_player_api.dto.CriarJogadorDTO;
import com.github.edusants22.cs_player_api.dto.JogadorResumoDTO;
import com.github.edusants22.cs_player_api.model.Jogador;
import com.github.edusants22.cs_player_api.model.enums.Funcao;
import com.github.edusants22.cs_player_api.model.enums.Pais;
import com.github.edusants22.cs_player_api.model.enums.Time;
import com.github.edusants22.cs_player_api.service.JogadorService;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jogadores")
public class JogadorController {

    private final JogadorService jogadorService;

    public JogadorController(JogadorService jogadorService){
        this.jogadorService = jogadorService;
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
            @RequestParam(required = false) Time time,
            @RequestParam(required = false) Funcao funcao) {
                return jogadorService.listaJogadores(time, pais, funcao);
    }

    @GetMapping("/listaJogadoresRating")
    public List <Jogador> listaJogadoresRating(
            @RequestParam(required = false) Pais pais,
            @RequestParam(required = false) Time time,
            @RequestParam(required = false) Double rating ){
                return jogadorService.listaJogadoresRating(pais, time, rating);
    }

    @GetMapping("/listaResumoJogadores")
    public List<JogadorResumoDTO> listaResumoJogadores(){
        return jogadorService.listaResumoJogadores();
    }

    @PostMapping
    public Jogador criarJogador(@RequestBody CriarJogadorDTO dto){
        return jogadorService.criarJogador(dto);
    }
}
