package com.github.edusants22.cs_player_api.dto;

public class JogadorResumoDTO {

    private String nickname;
    private Double rating;

    public JogadorResumoDTO(String nickname, Double rating) {
        this.nickname = nickname;
        this.rating = rating;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
