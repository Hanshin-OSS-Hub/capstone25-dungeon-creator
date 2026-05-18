package com.capstone.game_backend.domain.ranking;

import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rankings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity userEntity;

    private int bestPlayTime;

    //생성자
    @Builder
    public RankingEntity(UserEntity userEntity, int bestPlayTime) {
        this.userEntity = userEntity;
        this.bestPlayTime = bestPlayTime;
    }

    //랭킹 업데이트
    public void updateBestPlayTime(int newPlayTime) {
        this.bestPlayTime = newPlayTime;
    }
}

