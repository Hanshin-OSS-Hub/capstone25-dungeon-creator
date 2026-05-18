package com.capstone.game_backend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUid(String uid);
    Optional<UserEntity> findByNickname(String nickname);
    boolean existsByUid(String uid);
    boolean existsByNickname(String nickname);
}
