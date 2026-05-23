package com.capstone.game_backend.api;

import com.capstone.game_backend.domain.user.dto.UserLoginRequest;
import com.capstone.game_backend.domain.user.dto.UserResponse;
import com.capstone.game_backend.domain.user.dto.UserSignupRequest;
import com.capstone.game_backend.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody UserSignupRequest req){
        return userService.signup(req);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody UserLoginRequest req){
        return userService.login(req);
    }
}
