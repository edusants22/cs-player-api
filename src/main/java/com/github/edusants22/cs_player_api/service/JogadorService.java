package com.github.edusants22.cs_player_api.service;

import com.github.edusants22.cs_player_api.dto.CriarJogadorDTO;
import com.github.edusants22.cs_player_api.dto.JogadorResumoDTO;
import com.github.edusants22.cs_player_api.model.Jogador;
import com.github.edusants22.cs_player_api.model.enums.Funcao;
import com.github.edusants22.cs_player_api.model.enums.Pais;
import com.github.edusants22.cs_player_api.model.enums.Time;
import com.github.edusants22.cs_player_api.repository.JogadorRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class JogadorService {

    // O Service não sabe mais de onde vêm os jogadores.
    // Ele apenas conversa com o Repository.
    private final JogadorRepository repository;

    // O Spring cria automaticamente o Repository e entrega para o Service.
    // Isso é chamado de Injeção de Dependência.
    public JogadorService(JogadorRepository repository) {
        this.repository = repository;
    }

    // Retorna todos os jogadores.
    // Quem realmente possui a lista é o Repository.
    public List<Jogador> listarTodos() {
        return repository.listarTodos();
    }

    // Busca um jogador pelo ID.
    public Optional<Jogador> buscarPorId(Long id) {

        return repository.listarTodos()
                .stream()

                // Percorre toda a lista procurando o jogador
                // cujo id seja igual ao informado.
                .filter(jogador -> jogador.getId().equals(id))

                // Como o ID é único,
                // retorna apenas o primeiro encontrado.
                .findFirst();
    }

    // Retorna somente jogadores de um determinado país.
    public List<Jogador> buscarPorPais(Pais pais) {

        return repository.listarTodos()
                .stream()

                // Mantém apenas jogadores do país informado.
                .filter(jogador -> jogador.getPais().equals(pais))

                .toList();
    }

    // Permite pesquisar utilizando vários filtros.
    // Qualquer filtro pode ser nulo.
    public List<Jogador> listaJogadores(Time time,
                                        Pais pais,
                                        Funcao funcao) {

        return repository.listarTodos()
                .stream()

                // Se pais for null,
                // todos os jogadores passam.
                .filter(jogador ->
                        pais == null ||
                                jogador.getPais().equals(pais))

                // Mesmo raciocínio para função.
                .filter(jogador ->
                        funcao == null ||
                                jogador.getFuncao().equals(funcao))

                // Mesmo raciocínio para time.
                .filter(jogador ->
                        time == null ||
                                jogador.getTime().equals(time))

                .toList();
    }

    // Pesquisa utilizando país, time e rating mínimo.
    public List<Jogador> listaJogadoresRating(Pais pais,
                                              Time time,
                                              Double rating) {

        return repository.listarTodos()
                .stream()

                .filter(jogador ->
                        pais == null ||
                                jogador.getPais().equals(pais))

                .filter(jogador ->
                        time == null ||
                                jogador.getTime().equals(time))

                // Se o usuário informar um rating,
                // retorna somente jogadores com rating maior ou igual.
                .filter(jogador ->
                        rating == null ||
                                jogador.getRating() >= rating)

                // Ordena do maior rating para o menor.
                .sorted(
                        Comparator
                                .comparing(Jogador::getRating)
                                .reversed()
                )

                .toList();
    }

    // Cria uma lista resumida.
    // Em vez de devolver todos os dados do jogador,
    // devolve apenas nickname e rating.
    public List<JogadorResumoDTO> listaResumoJogadores() {

        return repository.listarTodos().stream()
                // Converte cada Jogador em um DTO.
                .map(jogador ->
                        new JogadorResumoDTO(
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
        jogador.setIdade(dto.getIdade());
        jogador.setPais(dto.getPais());
        jogador.setTime(dto.getTime());
        jogador.setFuncao(dto.getFuncao());
        jogador.setRating(dto.getRating());

        repository.listarTodos().add(jogador);

        return jogador;
    }
}