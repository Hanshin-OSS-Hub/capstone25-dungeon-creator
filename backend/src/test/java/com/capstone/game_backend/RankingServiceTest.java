package com.capstone.game_backend;

import com.capstone.game_backend.domain.ranking.RankingEntity;
import com.capstone.game_backend.domain.ranking.RankingRepository;
import com.capstone.game_backend.domain.ranking.RankingService;
import com.capstone.game_backend.domain.ranking.dto.RankingResponse;
import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RankingService rankingService;

    // 테스트용 UserEntity 생성 헬퍼 메서드
    private UserEntity createUser(Long id, String uid, String nickname) {
        UserEntity user = UserEntity.builder()
                .uid(uid)
                .nickname(nickname)
                .passwordHash("encodedPassword")
                .build();
        ReflectionTestUtils.setField(user, "id", id); // JPA가 부여하는 ID를 리플렉션으로 세팅
        return user;
    }

    // 테스트용 RankingEntity 생성 헬퍼 메서드
    private RankingEntity createRanking(Long id, UserEntity user, int bestPlayTime) {
        RankingEntity ranking = RankingEntity.builder()
                .user(user)
                .bestPlayTime(bestPlayTime)
                .build();
        ReflectionTestUtils.setField(ranking, "id", id);
        ReflectionTestUtils.setField(ranking, "updatedAt", LocalDateTime.now());
        return ranking;
    }

    @Test
    @DisplayName("랭킹 TOP 100 조회 - 순위가 1부터 순차적으로 부여되어야 한다")
    void getTop100Ranking_Success() {
        // given
        UserEntity user1 = createUser(1L, "uid1", "UserA");
        UserEntity user2 = createUser(2L, "uid2", "UserB");

        RankingEntity rank1 = createRanking(1L, user1, 100);
        RankingEntity rank2 = createRanking(2L, user2, 120);

        given(rankingRepository.findTop100ByOrderByBestPlayTimeAscUpdatedAtAsc())
                .willReturn(List.of(rank1, rank2));

        // when
        List<RankingResponse> result = rankingService.getTop100Ranking();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).rank()).isEqualTo(1);
        assertThat(result.get(0).nickname()).isEqualTo("UserA");
        assertThat(result.get(1).rank()).isEqualTo(2);
        assertThat(result.get(1).nickname()).isEqualTo("UserB");
    }

    @Test
    @DisplayName("닉네임으로 랭킹 조회 - 랭킹 기록이 있는 경우 계산된 순위를 반환한다")
    void getRankingByNickname_Success_Ranked() {
        // given
        String nickname = "testUser";
        UserEntity user = createUser(1L, "testUid", nickname);
        RankingEntity ranking = createRanking(1L, user, 150);

        given(userRepository.findByNickname(nickname)).willReturn(Optional.of(user));
        given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.of(ranking));

        // 내 기록보다 상위 기록이 5개 있다고 가정 -> 내 순위는 6위
        given(rankingRepository.calculateMyRank(eq(150), any(LocalDateTime.class))).willReturn(5L);

        // when
        RankingResponse response = rankingService.getRankingByNickname(nickname);

        // then
        assertThat(response.rank()).isEqualTo(6);
        assertThat(response.nickname()).isEqualTo(nickname);
        assertThat(response.bestPlayTime()).isEqualTo(150);
    }

    @Test
    @DisplayName("랭킹 갱신 로직 - 기존 기록보다 더 짧은(좋은) 시간이면 갱신한다")
    void updatePlayTimeIfBest_UpdateExisting() {
        // given
        UserEntity user = createUser(1L, "uid1", "UserA");
        RankingEntity existingRanking = createRanking(1L, user, 200);

        given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.of(existingRanking));

        // when (신기록 150 달성)
        rankingService.updatePlayTimeIfBest(user, 150);

        // then (실제 객체의 값이 150으로 변경되었는지 확인)
        assertThat(existingRanking.getBestPlayTime()).isEqualTo(150);
        verify(rankingRepository, never()).save(any());
    }

    @Test
    @DisplayName("랭킹 갱신 로직 - 랭킹 기록이 없으면 새롭게 생성하여 저장한다")
    void updatePlayTimeIfBest_CreateNew() {
        // given
        UserEntity user = createUser(1L, "uid1", "UserA");
        given(rankingRepository.findByUser_Id(1L)).willReturn(Optional.empty());

        // when
        rankingService.updatePlayTimeIfBest(user, 300);

        // then
        verify(rankingRepository, times(1)).save(any(RankingEntity.class));
    }
}