package com.capstone.game_backend.domain.record.repository;

import com.capstone.game_backend.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {

    List<Record> findByUserIdOrderByClearTimeSecondsAsc(Long userId);
    // 전적이 많아질 경우 메모리 초과 가능성 -> 페이징
    // 전적이 많아질 경우 정렬 속도 문제 -> 복합 인덱스
}
