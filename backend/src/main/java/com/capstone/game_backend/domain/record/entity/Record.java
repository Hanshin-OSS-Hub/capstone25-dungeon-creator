package com.capstone.game_backend.domain.record.entity;

import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "json")
    private String gameMeta; // JSON 문자열로 저장

    private int playTimeSeconds;

    //생성자
    @Builder
    public Record(User user, String gameMeta, int playTimeSeconds) {
        this.user = user;
        this.gameMeta = gameMeta;
        this.playTimeSeconds = playTimeSeconds;
    }

}

