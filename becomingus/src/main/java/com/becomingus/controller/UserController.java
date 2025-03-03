package com.becomingus.controller;

import com.becomingus.controller.dto.LoginRequestDto;
import com.becomingus.controller.dto.LoginResponseDto;
import com.becomingus.controller.dto.UserRequestDto;
import com.becomingus.domain.user.Role;
import com.becomingus.domain.user.User;
import com.becomingus.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 관련 API")
public class UserController {

    private final UserService userService;
    private final MessageSource messageSource;

    // 회원가입 API
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDto requestDto, Locale locale) {
        String message = userService.registerUser(requestDto.username(), requestDto.password(), requestDto.displayName(), Role.USER, locale);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자가 로그인합니다.")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        String token = userService.authenticateUser(requestDto.username(), requestDto.password());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
