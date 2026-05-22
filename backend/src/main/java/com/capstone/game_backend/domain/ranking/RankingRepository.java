package com.capstone.game_backend.domain.ranking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RankingRepository extends JpaRepository<RankingEntity, Long> {

    Optional<RankingEntity> findByUser_Id(Long userId);

    // 클리어타임 빠른순 정렬, 같으면 업데이트 시간 빠른순 정렬
    @EntityGraph(attributePaths = {"user"})
    List<RankingEntity> findTop100ByOrderByBestPlayTimeAscUpdatedAtAsc();

    // 특정 유저의 등수 계산
    @Query("SELECT COUNT(r) FROM Ranking r WHERE r.bestPlayTime < :myPlayTime OR " +
            "(r.bestPlayTime = :myPlayTime AND r.updatedAt < :myUpdatedAt)")
    long calculateMyRank(
            @Param("myPlayTime") int myPlayTime,
            @Param("myUpdatedAt") LocalDateTime myUpdatedAt
    );
}
