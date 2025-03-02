package com.becomingus.service;

import com.becomingus.domain.user.Role;
import com.becomingus.domain.user.User;
import com.becomingus.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
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

    // 회원가입 기능
    public User registerUser(String username, String password, String displayName, Role role) {
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // User 객체 생성 후 저장
        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .displayName(displayName)
                .role(role)
                .build();

        return userRepository.save(user);
    }

    // 로그인 시 사용자 조회(UserDetailsService의 메서드 구현, Spring Security)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user.notfound", new Object[]{username}, Locale.getDefault()));
    }
}
