package com.github.edusants22.cs_player_api.jogador;

import com.github.edusants22.cs_player_api.jogador.dto.CriarJogadorDTO;
import com.github.edusants22.cs_player_api.jogador.dto.JogadorResumoDTO;
import com.github.edusants22.cs_player_api.jogador.models.Jogador;
import com.github.edusants22.cs_player_api.jogador.enums.Funcao;
import com.github.edusants22.cs_player_api.jogador.enums.Pais;
import com.github.edusants22.cs_player_api.time.models.Time;
import com.github.edusants22.cs_player_api.time.TimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JogadorService {

    private final JogadorRepository jogadorRepository;
    private final TimeRepository timeRepository;

    public JogadorService(JogadorRepository jogadorRepository, TimeRepository timeRepository) {
        this.jogadorRepository = jogadorRepository;
        this.timeRepository = timeRepository;
    }

    // Retorna todos os jogadores.
    // Quem realmente possui a lista é o Repository.
    public List<Jogador> listarTodos() {
        return jogadorRepository.findAll();
    }

    // Busca um jogador pelo ID.
    public Optional<Jogador> buscarPorId(Long id) {
        return jogadorRepository.findById(id);
    }

    // Retorna somente jogadores de um determinado país.
    public List<Jogador> buscarPorPais(Pais pais) {
        return jogadorRepository.findByPais(pais);
    }

    // Permite pesquisar utilizando vários filtros.
    // Qualquer filtro pode ser nulo.
    public List<Jogador> listaJogadores(Long timeId,
                                        Pais pais,
                                        Funcao funcao) {
        Time time = buscarTime(timeId);
        return jogadorRepository.listaJogadores(pais, time, funcao);
    }

    // Pesquisa utilizando país, time e rating mínimo.
    public List<Jogador> listaJogadoresRating(Pais pais,
                                              Long timeId) {
        Time time = buscarTime(timeId);
        return  jogadorRepository.findByPaisAndTimeOrderByRatingDesc(pais, time);
    }

    // Cria uma lista resumida.
    // Em vez de devolver todos os dados do jogador,
    // devolve apenas nickname e rating.
    public List<JogadorResumoDTO> listaResumoJogadores() {

        return jogadorRepository.findAll()
                .stream()
                .map(jogador -> new JogadorResumoDTO(
                                jogador.getNickname(),
                                jogador.getRating()
                        ))
                .toList();
    }

    // Adiciona um novo jogador na "base de dados".
    // Hoje nossa base é uma ArrayList.
    // No futuro será um banco de dados.
    public Jogador criarJogador(CriarJogadorDTO dto) {

        Jogador jogador = new Jogador();

        jogador.setNickname(dto.getNickname());
        jogador.setNomeCompleto(dto.getNomeCompleto());
        jogador.setIdade(dto.getIdade());
        jogador.setPais(dto.getPais());
        jogador.setTime(buscarTime(dto.getTimeId()));
        jogador.setFuncao(dto.getFuncao());
        jogador.setArmaFavorita(dto.getArmaFavorita());
        jogador.setRating(dto.getRating());

        return jogadorRepository.save(jogador);
    }

    private Time buscarTime(Long timeId) {
        if (timeId == null) {
            return null;
        }

        return timeRepository.findById(timeId)
                .orElse(null);
    }
}