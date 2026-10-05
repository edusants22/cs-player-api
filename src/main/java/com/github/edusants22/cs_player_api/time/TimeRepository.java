package com.github.edusants22.cs_player_api.time;

import com.github.edusants22.cs_player_api.time.models.Time;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeRepository extends JpaRepository<Time,Long> {
}
