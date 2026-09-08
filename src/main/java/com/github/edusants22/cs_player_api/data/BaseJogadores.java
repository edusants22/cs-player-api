package com.github.edusants22.cs_player_api.data;


import com.github.edusants22.cs_player_api.model.Jogador;
import com.github.edusants22.cs_player_api.model.enums.ArmaFavorita;
import com.github.edusants22.cs_player_api.model.enums.Funcao;
import com.github.edusants22.cs_player_api.model.enums.Pais;
import com.github.edusants22.cs_player_api.model.enums.Time;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BaseJogadores {

    public static List<Jogador> carregar() {

        List<Jogador> jogadores = new ArrayList<>();
        jogadores.add(new Jogador(
                1L,
                "FalleN",
                "Gabriel Toledo",
                35,
                Pais.BRASIL,
                Time.FURIA,
                Funcao.AWPER,
                ArmaFavorita.AWP,
                1.08,
                2,
                LocalDate.of(2005, 1, 1)
        ));

        jogadores.add(new Jogador(
                2L,
                "KSCERATO",
                "Kaike Cerato",
                26,
                Pais.BRASIL,
                Time.FURIA,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.18,
                0,
                LocalDate.of(2017, 1, 1)
        ));

        jogadores.add(new Jogador(
                3L,
                "yuurih",
                "Yuri Santos",
                26,
                Pais.BRASIL,
                Time.FURIA,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.16,
                0,
                LocalDate.of(2017, 1, 1)
        ));

        jogadores.add(new Jogador(
                4L,
                "YEKINDAR",
                "Mareks Gaļinskis",
                25,
                Pais.LETONIA,
                Time.LIQUID,
                Funcao.ENTRY,
                ArmaFavorita.AK47,
                1.10,
                0,
                LocalDate.of(2017, 1, 1)
        ));

        jogadores.add(new Jogador(
                5L,
                "NiKo",
                "Nikola Kovač",
                29,
                Pais.BOSNIA,
                Time.FAZE,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.23,
                0,
                LocalDate.of(2015, 1, 1)
        ));

        jogadores.add(new Jogador(
                6L,
                "m0NESY",
                "Ilya Osipov",
                21,
                Pais.RUSSIA,
                Time.G2,
                Funcao.AWPER,
                ArmaFavorita.AWP,
                1.27,
                0,
                LocalDate.of(2021, 1, 1)
        ));

        jogadores.add(new Jogador(
                7L,
                "huNter-",
                "Nemanja Kovač",
                30,
                Pais.BOSNIA,
                Time.G2,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.12,
                0,
                LocalDate.of(2015, 1, 1)
        ));

        jogadores.add(new Jogador(
                8L,
                "donk",
                "Danil Kryshkovets",
                18,
                Pais.RUSSIA,
                Time.SPIRIT,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.37,
                1,
                LocalDate.of(2023, 1, 1)
        ));

        jogadores.add(new Jogador(
                9L,
                "sh1ro",
                "Dmitry Sokolov",
                24,
                Pais.RUSSIA,
                Time.SPIRIT,
                Funcao.AWPER,
                ArmaFavorita.AWP,
                1.22,
                1,
                LocalDate.of(2018, 1, 1)
        ));

        jogadores.add(new Jogador(
                10L,
                "chopper",
                "Leonid Vishnyakov",
                28,
                Pais.RUSSIA,
                Time.SPIRIT,
                Funcao.IGL,
                ArmaFavorita.M4A4,
                1.05,
                1,
                LocalDate.of(2016, 1, 1)
        ));

        jogadores.add(new Jogador(
                11L,
                "ZywOo",
                "Mathieu Herbaut",
                25,
                Pais.FRANCA,
                Time.VITALITY,
                Funcao.AWPER,
                ArmaFavorita.AWP,
                1.35,
                1,
                LocalDate.of(2018, 1, 1)
        ));

        jogadores.add(new Jogador(
                12L,
                "apEX",
                "Dan Madesclaire",
                32,
                Pais.FRANCA,
                Time.VITALITY,
                Funcao.IGL,
                ArmaFavorita.M4A4,
                1.03,
                2,
                LocalDate.of(2013, 1, 1)
        ));

        jogadores.add(new Jogador(
                13L,
                "flameZ",
                "Shahar Shushan",
                22,
                Pais.ISRAEL,
                Time.VITALITY,
                Funcao.ENTRY,
                ArmaFavorita.AK47,
                1.13,
                1,
                LocalDate.of(2021, 1, 1)
        ));

        jogadores.add(new Jogador(
                14L,
                "ropz",
                "Robin Kool",
                26,
                Pais.ESTONIA,
                Time.VITALITY,
                Funcao.LURKER,
                ArmaFavorita.M4A1S,
                1.20,
                2,
                LocalDate.of(2016, 1, 1)
        ));

        jogadores.add(new Jogador(
                15L,
                "b1t",
                "Valerii Vakhovskyi",
                22,
                Pais.UCRANIA,
                Time.NAVI,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.15,
                1,
                LocalDate.of(2020, 1, 1)
        ));

        jogadores.add(new Jogador(
                16L,
                "w0nderful",
                "Ihor Zhdanov",
                21,
                Pais.UCRANIA,
                Time.NAVI,
                Funcao.AWPER,
                ArmaFavorita.AWP,
                1.16,
                0,
                LocalDate.of(2021, 1, 1)
        ));

        jogadores.add(new Jogador(
                17L,
                "Aleksib",
                "Aleksi Virolainen",
                29,
                Pais.FINLANDIA,
                Time.NAVI,
                Funcao.IGL,
                ArmaFavorita.M4A1S,
                1.01,
                1,
                LocalDate.of(2018, 1, 1)
        ));

        jogadores.add(new Jogador(
                18L,
                "torzsi",
                "Ádám Torzsás",
                23,
                Pais.HUNGRIA,
                Time.MOUZ,
                Funcao.AWPER,
                ArmaFavorita.AWP,
                1.14,
                0,
                LocalDate.of(2021, 1, 1)
        ));

        jogadores.add(new Jogador(
                19L,
                "xertioN",
                "Dorian Berman",
                21,
                Pais.ISRAEL,
                Time.MOUZ,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.15,
                0,
                LocalDate.of(2022, 1, 1)
        ));

        jogadores.add(new Jogador(
                20L,
                "Jimpphat",
                "Jimi Salo",
                19,
                Pais.FINLANDIA,
                Time.MOUZ,
                Funcao.RIFLER,
                ArmaFavorita.AK47,
                1.17,
                0,
                LocalDate.of(2023, 1, 1)
        ));

        return jogadores;
    }

}
