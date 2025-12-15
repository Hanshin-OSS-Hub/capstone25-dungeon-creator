package com.capstone.game_backend.domain.user.entity;

import com.capstone.game_backend.domain.ranking.entity.Ranking;
import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uid;     //로그인용 ID

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String passwordHash;

    //생성자
    @Builder
    public User(Long id, String uid, String nickname, String passwordHash) {
        this.id = id;
        this.uid = uid;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
    }
    
    //닉네임 변경
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
}
