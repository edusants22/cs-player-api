package com.github.edusants22.cs_player_api.repository;

import com.github.edusants22.cs_player_api.data.BaseJogadores;
import com.github.edusants22.cs_player_api.model.Jogador;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JogadorRepository {

    private final List<Jogador> jogadores = BaseJogadores.carregar();

    public List<Jogador> listarTodos() {
        return jogadores;
    }

    public List<Jogador> findAll(){
        return jogadores;
    }

}
