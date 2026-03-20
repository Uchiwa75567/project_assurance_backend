package com.ma_sante_assurance.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ma_sante_assurance.auth.controller.AuthController;
import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.auth.dto.AuthResponseDTO;
import com.ma_sante_assurance.auth.service.AuthService;
import com.ma_sante_assurance.auth.service.IssuedSession;
import com.ma_sante_assurance.common.enums.UserRole;
import com.ma_sante_assurance.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
