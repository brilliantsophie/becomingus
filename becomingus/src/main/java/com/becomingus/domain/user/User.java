package com.becomingus.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 기본키

    @Column(nullable = false, unique = true)
    private String username;    // 사용자 ID (이메일 or 유저네임)

    @Column(nullable = false)
    private String password;    // 비밀번호 (BCrypt 암호화 필요)

    @Column(nullable = false)
    private String displayName; // 화면에 표시할 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // 역할 (USER, ADMIN)

    // 권한 관련 메서드 (Spring Security용)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + role.name());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
