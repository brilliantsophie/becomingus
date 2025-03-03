package com.becomingus.controller;

import com.becomingus.controller.dto.LoginRequestDto;
import com.becomingus.controller.dto.UserRequestDto;
import com.becomingus.domain.user.Role;
import com.becomingus.domain.user.User;
import com.becomingus.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // 컨트롤러를 수동으로 MockMvc에 등록
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerUser_Success() throws Exception {
        // given (Mock 데이터 설정)
        UserRequestDto requestDto = new UserRequestDto("testUser", "testPass123", "테스트 유저");
        String successMessage = "회원가입이 완료되었습니다.";
        Locale locale = Locale.KOREAN;

        Mockito.when(userService.registerUser(anyString(), anyString(), anyString(), any(Role.class), any(Locale.class)))
                .thenReturn(successMessage);

        Mockito.when(messageSource.getMessage(eq("user.register.success"), isNull(), eq(locale)))
                .thenReturn(successMessage);

        // when & then (API 호출 및 검증)
        mockMvc.perform(post("/api/users/register")
                .contentType("application/json;charset=UTF-8")
                .characterEncoding("UTF-8") // 요청 인코딩 명시적으로 설정
                .content(objectMapper.writeValueAsString(requestDto))
                .header("Accept-Language", "ko"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseEncoding = result.getResponse().getCharacterEncoding();
                    assertEquals("UTF-8", responseEncoding); // 인코딩 검증
                })
                .andExpect(content().string(successMessage));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginUser_Success() throws Exception {
        // given (Mock 데이터 설정)
        LoginRequestDto requestDto = new LoginRequestDto("testUser", "testPass123");
        String mockToken = "Bearer testUser_TOKEN";

        Mockito.when(userService.authenticateUser(anyString(), anyString()))
                .thenReturn(mockToken);

        // when & then (API 호출 및 검증)
        mockMvc.perform(post("/api/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(mockToken));
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void loginUser_Fail_UserNotFound() throws Exception {
        // given
        LoginRequestDto requestDto = new LoginRequestDto("wrongUser", "testPass123");

        Mockito.when(userService.authenticateUser(anyString(), anyString()))
                .thenThrow(new RuntimeException("사용자를 찾을 수 없습니다: wrongUser"));

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("사용자를 찾을 수 없습니다: wrongUser"));
    }

}