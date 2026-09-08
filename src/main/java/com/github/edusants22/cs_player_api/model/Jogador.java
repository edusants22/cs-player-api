package com.github.edusants22.cs_player_api.model;

import com.github.edusants22.cs_player_api.model.enums.ArmaFavorita;
import com.github.edusants22.cs_player_api.model.enums.Funcao;
import com.github.edusants22.cs_player_api.model.enums.Pais;
import com.github.edusants22.cs_player_api.model.enums.Time;

import java.time.LocalDate;
import java.time.Period;

public class Jogador {

    private Long id;
    private String nickname;
    private String nomeCompleto;
    private Integer idade;
    private Pais pais;
    private Time time;
    private Funcao funcao;
    private ArmaFavorita armaFavorita;
    private Double rating;
    private Integer majors;
    private LocalDate inicioCarreira;

    public Jogador(Long id,
                   String nickname,
                   String nomeCompleto,
                   Integer idade,
                   Pais pais,
                   Time time,
                   Funcao funcao,
                   ArmaFavorita armaFavorita,
                   Double rating,
                   Integer majors,
                   LocalDate inicioCarreira) {
        this.id = id;
        this.nickname = nickname;
        this.nomeCompleto = nomeCompleto;
        this.idade = idade;
        this.pais = pais;
        this.time = time;
        this.funcao = funcao;
        this.armaFavorita = armaFavorita;
        this.rating = rating;
        this.majors = majors;
        this.inicioCarreira = inicioCarreira;
    }

    public Jogador(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
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

    public ArmaFavorita getArmaFavorita() {
        return armaFavorita;
    }

    public void setArmaFavorita(ArmaFavorita armaFavorita) {
        this.armaFavorita = armaFavorita;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getMajors() {
        return majors;
    }

    public void setMajors(Integer majors) {
        this.majors = majors;
    }

    public LocalDate getInicioCarreira() {
        return inicioCarreira;
    }

    public void setInicioCarreira(LocalDate inicioCarreira) {
        this.inicioCarreira = inicioCarreira;
    }
}