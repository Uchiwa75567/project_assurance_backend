package com.ma_sante_assurance.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma_sante_assurance.auth.controller.AuthController;
import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.auth.dto.AuthResponseDTO;
import com.ma_sante_assurance.auth.service.AuthService;
import com.ma_sante_assurance.auth.service.IssuedSession;
import com.ma_sante_assurance.common.enums.UserRole;
import com.ma_sante_assurance.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Test
    void loginShouldReturnSessionEnvelope() throws Exception {
        AuthResponseDTO.SessionResponse response = new AuthResponseDTO.SessionResponse(
                900, 1209600,
                "u1", "User One", "u1@mail.com", UserRole.ADMIN
        );

        IssuedSession issuedSession = new IssuedSession(
                "access-token",
                "refresh-token",
                response
        );

        Mockito.when(authService.login(Mockito.any())).thenReturn(issuedSession);

        AuthRequestDTO.LoginRequest body = new AuthRequestDTO.LoginRequest("u1@mail.com", "pass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value("u1"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"))
                .andExpect(jsonPath("$.data.accessTokenExpiresIn").value(900))
                .andExpect(jsonPath("$.data.refreshTokenExpiresIn").value(1209600));
    }

    @Test
    void registerShouldReturnSessionEnvelopeAndCookies() throws Exception {
        AuthResponseDTO.SessionResponse response = new AuthResponseDTO.SessionResponse(
                900, 1209600,
                "u2", "User Two", "u2@mail.com", UserRole.CLIENT
        );

        IssuedSession issuedSession = new IssuedSession(
                "access-token",
                "refresh-token",
                response
        );

        Mockito.when(authService.register(Mockito.any())).thenReturn(issuedSession);

        AuthRequestDTO.RegisterRequest body = new AuthRequestDTO.RegisterRequest(
                "User Two",
                "u2@mail.com",
                LocalDate.of(1998, 5, 18),
                "+221771234567",
                "123456789",
                null,
                "pass",
                UserRole.CLIENT
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value("u2"))
                .andExpect(jsonPath("$.data.role").value("CLIENT"))
                .andExpect(result -> {
                    List<String> cookies = result.getResponse().getHeaders("Set-Cookie");
                    assertTrue(cookies.stream().anyMatch(value -> value.contains("msa_access_token=access-token")));
                    assertTrue(cookies.stream().anyMatch(value -> value.contains("msa_refresh_token=refresh-token")));
                });
    }

    @Test
    void refreshShouldUseRefreshCookieAndReturnNewSession() throws Exception {
        AuthResponseDTO.SessionResponse response = new AuthResponseDTO.SessionResponse(
                900, 1209600,
                "u3", "User Three", "u3@mail.com", UserRole.AGENT
        );

        IssuedSession issuedSession = new IssuedSession(
                "new-access-token",
                "new-refresh-token",
                response
        );

        Mockito.when(authService.refresh("refresh-token")).thenReturn(issuedSession);

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("msa_refresh_token", "refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value("u3"))
                .andExpect(jsonPath("$.data.role").value("AGENT"))
                .andExpect(result -> {
                    List<String> cookies = result.getResponse().getHeaders("Set-Cookie");
                    assertTrue(cookies.stream().anyMatch(value -> value.contains("msa_access_token=new-access-token")));
                    assertTrue(cookies.stream().anyMatch(value -> value.contains("msa_refresh_token=new-refresh-token")));
                });
    }

    @Test
    void meShouldReadBearerTokenAndReturnSession() throws Exception {
        AuthResponseDTO.SessionResponse response = new AuthResponseDTO.SessionResponse(
                900, 1209600,
                "u4", "User Four", "u4@mail.com", UserRole.CLIENT
        );

        Mockito.when(authService.me("access-token")).thenReturn(response);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value("u4"))
                .andExpect(jsonPath("$.data.fullName").value("User Four"))
                .andExpect(jsonPath("$.data.role").value("CLIENT"));
    }

    @Test
    void logoutShouldInvalidateRefreshCookieAndClearCookies() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new Cookie("msa_refresh_token", "refresh-token")))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<String> cookies = result.getResponse().getHeaders("Set-Cookie");
                    assertTrue(cookies.stream().anyMatch(value -> value.contains("msa_access_token=") && value.contains("Max-Age=0")));
                    assertTrue(cookies.stream().anyMatch(value -> value.contains("msa_refresh_token=") && value.contains("Max-Age=0")));
                });

        Mockito.verify(authService).logout("refresh-token");
    }

    @Test
    void verifyOtpShouldReturnOk() throws Exception {
        AuthRequestDTO.VerifyOtpRequest body = new AuthRequestDTO.VerifyOtpRequest("u2@mail.com", "123456");

        mockMvc.perform(post("/api/auth/verify-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Code OTP verifie"));

        Mockito.verify(authService).verifyOtp(Mockito.any());
    }

    @Test
    void resendOtpShouldReturnOk() throws Exception {
        AuthRequestDTO.ResendOtpRequest body = new AuthRequestDTO.ResendOtpRequest("u2@mail.com");

        mockMvc.perform(post("/api/auth/resend-otp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Code OTP renvoye"));

        Mockito.verify(authService).resendOtp(Mockito.any());
    }
}
