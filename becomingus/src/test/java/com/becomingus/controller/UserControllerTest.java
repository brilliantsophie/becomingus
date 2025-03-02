package com.becomingus.controller;

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
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerUser_Success() throws Exception {
        // given (Mock 데이터 설정)
        UserRequestDto requestDto = new UserRequestDto("testUser", "testPass123", "테스트 유저");
        String successMessage = "회원가입이 완료되었습니다.";

        Mockito.when(userService.registerUser(anyString(), anyString(), anyString(), any(Role.class)))
                .thenReturn(new User(1L, "testUser", "encodedPassword", "테스트 유저", Role.USER));

        Mockito.when(messageSource.getMessage(eq("user.register.success"), isNull(), any(Locale.class)))
                .thenReturn(successMessage);

        // when & then (API 호출 및 검증)
        mockMvc.perform(post("/api/users/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));

    }

}