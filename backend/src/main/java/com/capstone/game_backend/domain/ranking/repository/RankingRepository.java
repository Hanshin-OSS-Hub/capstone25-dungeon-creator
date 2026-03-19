package com.capstone.game_backend.domain.ranking.repository;

import com.capstone.game_backend.domain.ranking.entity.Ranking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<Ranking, Long> {

    Optional<Ranking> findByUserId(Long userId);

    // 점수 높은 순 정렬, 같으면 업데이트 시간 빠른순 정렬
    @EntityGraph(attributePaths = {"user"})
    List<Ranking> findTop100ByOrderByRankScoreDescUpdatedAtAsc();

    // 특정 유저의 등수 계산 (점수가 더 높거나, 점수동일에 업데이트 빠른 사람의 수)
    @Query("SELECT COUNT(r) FROM Ranking r WHERE r.rankScore > :myScore OR " +
            "(r.rankScore = :myScore AND r.updatedAt < :myUpdatedAt)")
    long calculateMyRank(
            @Param("myScore") int myScore,
            @Param("myUpdatedAt") LocalDateTime myUpdatedAt
    );
}
