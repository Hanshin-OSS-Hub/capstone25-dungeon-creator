package com.capstone.game_backend.domain.user.entity;

import com.capstone.game_backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false") //
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String uid;     //로그인용 ID

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean isDeleted = false; // 기본값은 false (활동 중)

    // 생성자
    @Builder
    public User(String uid, String nickname, String passwordHash) {
        this.uid = uid;
        this.nickname = nickname;
        this.passwordHash = passwordHash;
    }
    
    // 닉네임 변경
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    // 회원 탈퇴 (소프트 딜리트)
    public void withdraw() {
        this.isDeleted = true;
    }
}
