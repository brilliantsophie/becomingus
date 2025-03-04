package com.becomingus.service;

import com.becomingus.domain.user.Role;
import com.becomingus.domain.user.User;
import com.becomingus.domain.user.UserRepository;
import com.becomingus.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // 비밀번호 암호화
    private final MessageSource messageSource;  // 다국어 메시지 소스
    private final AuthenticationManager authenticationManager;

    // 회원가입 기능
    public String registerUser(String username, String password, String displayName, Role role, Locale locale) {
        // 기존 유저 중복 체크
        /* if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException(messageSource.getMessage("user.duplicate", null, locale));
        }
        */

        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(password);

        // User 객체 생성 후 저장
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .displayName(displayName)
                .role(role)
                .build();

        userRepository.save(user);

        return messageSource.getMessage("user.register.success", null, locale);
    }

    // 로그인 시 사용자 조회(UserDetailsService의 메서드 구현, Spring Security)
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(
                        messageSource.getMessage("user.notfound", new Object[]{username}, Locale.getDefault())
                ));

        return new CustomUserDetails(user); // User -> CustomUserDetails 변환
    }

    // 로그인 기능(인증)
    public String authenticateUser(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails user = loadUserByUsername(username);
        return "Bearer " + generateDummyToken(user.getUsername());  // (JWT 적용 시 수정 예정)
    }

    private String generateDummyToken(String username) {
        return username + "_TOKEN"; // JWT 적용 시 수정 예정
    }
}
