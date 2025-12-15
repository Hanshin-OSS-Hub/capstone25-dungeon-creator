package com.capstone.game_backend.domain.record.repository;

import com.capstone.game_backend.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByUserIdOrderByPlayedAtDesc(Long userId);
}
