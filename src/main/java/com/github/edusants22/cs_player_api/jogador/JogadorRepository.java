package com.github.edusants22.cs_player_api.jogador;

import com.github.edusants22.cs_player_api.jogador.models.Jogador;
import com.github.edusants22.cs_player_api.jogador.enums.Funcao;
import com.github.edusants22.cs_player_api.jogador.enums.Pais;
import com.github.edusants22.cs_player_api.time.models.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {
    
    List<Jogador> findByPais(Pais pais);

    @Query("""
    SELECT j FROM Jogador j
    WHERE (:pais IS NULL OR j.pais = :pais)
      AND (:time IS NULL OR j.time = :time)
      AND (:funcao IS NULL OR j.funcao = :funcao)
""")
    List<Jogador> listaJogadores(
            @Param("pais") Pais pais,
            @Param("time") Time time,
            @Param("funcao") Funcao funcao
    );

    List<Jogador> findByPaisAndTimeOrderByRatingDesc(Pais pais, Time time);

}
