package com.capstone.game_backend.domain.ranking.entity;

import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rankings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ranking extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private int rankScore;

    //생성자
    @Builder
    public Ranking(User user, int rankScore) {
        this.user = user;
        this.rankScore = rankScore;
    }

    //랭킹 업데이트
    public void updateScore(int rankScore) {
        this.rankScore = rankScore;
    }
}

