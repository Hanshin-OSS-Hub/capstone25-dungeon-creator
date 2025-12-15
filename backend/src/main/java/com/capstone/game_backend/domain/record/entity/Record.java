package com.capstone.game_backend.domain.record.entity;

import com.capstone.game_backend.domain.user.entity.User;
import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int score;

    @Column(columnDefinition = "json")
    private String gameMeta; // JSON 문자열로 저장

    private LocalDateTime playedAt;

    //생성자
    @Builder
    public Record(User user, int score, String gameMeta, LocalDateTime playedAt) {
        this.user = user;
        this.score = score;
        this.gameMeta = gameMeta;
        this.playedAt = playedAt;
    }

}

