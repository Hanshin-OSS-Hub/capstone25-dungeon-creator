package com.capstone.game_backend;

import com.capstone.game_backend.domain.user.UserEntity;
import com.capstone.game_backend.domain.user.UserRepository;
import com.capstone.game_backend.domain.user.UserService;
import com.capstone.game_backend.domain.user.dto.UserLoginRequest;
import com.capstone.game_backend.domain.user.dto.UserResponse;
import com.capstone.game_backend.domain.user.dto.UserSignupRequest;
import com.capstone.game_backend.global.error.CustomException;
import com.capstone.game_backend.global.error.ErrorCode;
import com.capstone.game_backend.global.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService; // 가짜 객체들을 주입받을 진짜 테스트 대상

    @Mock
    private UserRepository userRepository; // 가짜 DB

    @Mock
    private PasswordEncoder passwordEncoder; // 가짜 암호화기

    @Mock
    private JwtUtil jwtUtil; // 가짜 토큰 생성기

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given (준비)
        UserSignupRequest req = new UserSignupRequest("testUid", "testNickname", "password123");

        given(userRepository.existsByUid(req.uid())).willReturn(false);
        given(userRepository.existsByNickname(req.nickname())).willReturn(false);
        given(passwordEncoder.encode(req.password())).willReturn("encodedPassword");

        // when (실행)
        UserResponse response = userService.signup(req);

        // then (검증)
        assertThat(response.uid()).isEqualTo("testUid");
        assertThat(response.nickname()).isEqualTo("testNickname");
        verify(userRepository).save(any(UserEntity.class)); // save 메서드가 1번 호출되었는지 검증
    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 중복")
    void signup_fail_duplicateUid() {
        // given
        UserSignupRequest req = new UserSignupRequest("testUid", "testNickname", "password123");
        given(userRepository.existsByUid(req.uid())).willReturn(true); // 중복이라고 설정

        // when & then
        assertThatThrownBy(() -> userService.signup(req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_UID);
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        UserLoginRequest req = new UserLoginRequest("testUid", "password123");
        UserEntity userEntity = UserEntity.builder()
                .uid("testUid")
                .nickname("testNickname")
                .passwordHash("encodedPassword")
                .build();

        given(userRepository.findByUid(req.uid())).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(req.password(), "encodedPassword")).willReturn(true);
        given(jwtUtil.generateToken("testUid")).willReturn("mockToken");

        // when
        UserResponse response = userService.login(req);

        // then
        assertThat(response.uid()).isEqualTo("testUid");
        assertThat(response.token()).isEqualTo("mockToken"); // 토큰이 잘 들어갔는지 확인
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrongPassword() {
        // given
        UserLoginRequest req = new UserLoginRequest("testUid", "wrongPassword");
        UserEntity userEntity = UserEntity.builder()
                .uid("testUid")
                .passwordHash("encodedPassword")
                .build();

        given(userRepository.findByUid(req.uid())).willReturn(Optional.of(userEntity));
        given(passwordEncoder.matches(req.password(), "encodedPassword")).willReturn(false); // 불일치 설정

        // when & then
        assertThatThrownBy(() -> userService.login(req))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("유저 탈퇴 성공")
    void deleteUser_success() {
        // given
        String uid = "testUid";
        UserEntity userEntity = UserEntity.builder().uid(uid).build();

        given(userRepository.findByUid(uid)).willReturn(Optional.of(userEntity));

        // when
        userService.deleteUser(uid);

        // then
        // withdraw() 내부 구현에 따라 다르겠지만, 예를 들어 상태값이 변했다면 그걸 검증
        // 여기서는 에러 없이 무사히 통과하는지를 주로 봅니다.
    }
}
