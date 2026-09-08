package com.github.edusants22.cs_player_api.dto;

import com.github.edusants22.cs_player_api.model.enums.Funcao;
import com.github.edusants22.cs_player_api.model.enums.Pais;
import com.github.edusants22.cs_player_api.model.enums.Time;

public class CriarJogadorDTO {

    private String nickname;
    private Integer idade;
    private Pais pais;
    private Time time;
    private Funcao funcao;
    private Double rating;

    public CriarJogadorDTO(String nickname, Integer idade, Pais pais, Time time, Funcao funcao, Double rating) {
        this.nickname = nickname;
        this.idade = idade;
        this.pais = pais;
        this.time = time;
        this.funcao = funcao;
        this.rating = rating;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
