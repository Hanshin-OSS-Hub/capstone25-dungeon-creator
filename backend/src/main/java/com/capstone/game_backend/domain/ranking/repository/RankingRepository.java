package com.capstone.game_backend.domain.ranking.repository;

import com.capstone.game_backend.domain.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByuserId(Long userId);

    List<Ranking> findAllByOrderByRankPositionAsc();
}
