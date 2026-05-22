package com.capstone.game_backend;

import com.capstone.game_backend.domain.ranking.RankingService;
import com.capstone.game_backend.domain.record.RecordEntity;
import com.capstone.game_backend.domain.record.RecordRepository;
import com.capstone.game_backend.domain.record.RecordService;
import com.capstone.game_backend.domain.record.dto.RecordCreateRequest;
import com.capstone.game_backend.domain.record.dto.RecordResponse;
import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.domain.user.UserRepository;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @InjectMocks
    private RecordService recordService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private RankingService rankingService;

    // 💡 ObjectMapper는 가짜가 아니라 진짜 객체를 투입해서 실제 JSON 파싱을 하도록 만듭니다!
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("전적 생성 성공 - 게임 클리어 (랭킹 반영 O)")
    void create_success_isClearedTrue() {
        // given
        String uid = "testUid";
        String validJson = "{\"isCleared\": true, \"score\": 100}";
        RecordCreateRequest req = new RecordCreateRequest(validJson, 120);
        UserEntity userEntity = UserEntity.builder().uid(uid).build();
        org.springframework.test.util.ReflectionTestUtils.setField(userEntity, "id", 1L);

        given(userRepository.findByUid(uid)).willReturn(Optional.of(userEntity));

        // when
        RecordResponse response = recordService.create(uid, req);

        // then
        assertThat(response.gameMeta()).isEqualTo(validJson);
        assertThat(response.playTimeSeconds()).isEqualTo(120);

        verify(recordRepository).save(any(RecordEntity.class)); // DB 저장 확인
        verify(rankingService).updatePlayTimeIfBest(userEntity, 120); // 랭킹 업데이트 호출 확인
    }

    @Test
    @DisplayName("전적 생성 성공 - 게임 미클리어 (랭킹 반영 X)")
    void create_success_isClearedFalse() {
        // given
        String uid = "testUid";
        String validJson = "{\"isCleared\": false}";
        RecordCreateRequest req = new RecordCreateRequest(validJson, 50);
        UserEntity userEntity = UserEntity.builder().uid(uid).build();
        org.springframework.test.util.ReflectionTestUtils.setField(userEntity, "id", 1L);

        given(userRepository.findByUid(uid)).willReturn(Optional.of(userEntity));

        // when
        recordService.create(uid, req);

        // then
        verify(recordRepository).save(any(RecordEntity.class));
        verify(rankingService, never()).updatePlayTimeIfBest(any(), any(Integer.class)); // 💡 랭킹 업데이트가 '절대 호출되지 않아야' 성공!
    }

    @Test
    @DisplayName("전적 생성 실패 - JSON 형식이 깨진 경우")
    void create_fail_invalidJson() {
        // given
        String uid = "testUid";
        String brokenJson = "{isCleared: true"; // 따옴표가 빠진 깨진 JSON
        RecordCreateRequest req = new RecordCreateRequest(brokenJson, 120);
        UserEntity userEntity = UserEntity.builder().uid(uid).build();
        org.springframework.test.util.ReflectionTestUtils.setField(userEntity, "id", 1L);

        given(userRepository.findByUid(uid)).willReturn(Optional.of(userEntity));

        // when & then
        assertThatThrownBy(() -> recordService.create(uid, req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    @DisplayName("전적 조회 성공")
    void getRecords_success() {
        // given
        String nickname = "testNick";
        UserEntity userEntity = UserEntity.builder().nickname(nickname).build();
        org.springframework.test.util.ReflectionTestUtils.setField(userEntity, "id", 1L);

        RecordEntity record1 = RecordEntity.builder().gameMeta("{\"isCleared\":true}").playTimeSeconds(100).build();
        RecordEntity record2 = RecordEntity.builder().gameMeta("{\"isCleared\":false}").playTimeSeconds(150).build();

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(userEntity));
        given(recordRepository.findByUser_IdOrderByPlayTimeSecondsAsc(userEntity.getId()))
                .willReturn(List.of(record1, record2)); // 2개의 전적이 있다고 설정

        // when
        List<RecordResponse> responses = recordService.getRecords(nickname);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).playTimeSeconds()).isEqualTo(100);
        assertThat(responses.get(1).playTimeSeconds()).isEqualTo(150);
    }

    @Test
    @DisplayName("전적 조회 실패 - 존재하지 않는 닉네임")
    void getRecords_fail_userNotFound() {
        // given
        String nickname = "unknownNick";
        given(userRepository.findByNickname(nickname)).willReturn(Optional.empty()); // 유저 없음 설정

        // when & then
        assertThatThrownBy(() -> recordService.getRecords(nickname))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
    }
}
