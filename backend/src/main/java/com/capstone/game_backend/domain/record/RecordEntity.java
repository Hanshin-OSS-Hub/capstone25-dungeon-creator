package com.capstone.game_backend.domain.record;

import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(columnDefinition = "json")
    private String gameMeta; // JSON 문자열로 저장

    private int playTimeSeconds;

    //생성자
    @Builder
    public RecordEntity(UserEntity userEntity, String gameMeta, int playTimeSeconds) {
        this.userEntity = userEntity;
        this.gameMeta = gameMeta;
        this.playTimeSeconds = playTimeSeconds;
    }

}

