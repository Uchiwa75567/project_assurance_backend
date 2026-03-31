package com.ma_sante_assurance.auth.controller;

import com.ma_sante_assurance.auth.dto.AuthRequestDTO;
import com.ma_sante_assurance.auth.dto.AuthResponseDTO;
import com.ma_sante_assurance.auth.service.AuthService;
import com.ma_sante_assurance.auth.service.IssuedSession;
import com.ma_sante_assurance.common.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentification et gestion de session")
public class AuthController {

    private static final String ACCESS_COOKIE = "msa_access_token";
    private static final String REFRESH_COOKIE = "msa_refresh_token";

    private final AuthService authService;
    private final boolean secureCookies;

    public AuthController(AuthService authService, @Value("${app.security.secure-cookies:false}") boolean secureCookies) {
        this.authService = authService;
        this.secureCookies = secureCookies;
    }

    @GetMapping("/csrf")
    @Operation(summary = "CSRF token", description = "Retourne un token CSRF stocke en cookie pour les requetes mutantes.")
    public ApiResponse<AuthResponseDTO.CsrfResponse> csrf(CsrfToken token) {
        return ApiResponse.ok("CSRF token", new AuthResponseDTO.CsrfResponse(token.getToken()));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription", description = "Cree un compte utilisateur et ouvre une session.")
    public ApiResponse<AuthResponseDTO.SessionResponse> register(@Valid @RequestBody AuthRequestDTO.RegisterRequest request,
                                                                  HttpServletResponse response) {
        IssuedSession issued = authService.register(request);
        attachCookies(response, issued.accessToken(), issued.refreshToken(), issued.session());
        return ApiResponse.ok("Inscription reussie", issued.session());
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Authentifie l'utilisateur et retourne une session.")
    public ApiResponse<AuthResponseDTO.SessionResponse> login(@Valid @RequestBody AuthRequestDTO.LoginRequest request,
                                                               HttpServletResponse response) {
        IssuedSession issued = authService.login(request);
        attachCookies(response, issued.accessToken(), issued.refreshToken(), issued.session());
        return ApiResponse.ok("Connexion reussie", issued.session());
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rafraichir session", description = "Renouvelle l'access token a partir du refresh cookie.")
    public ApiResponse<AuthResponseDTO.SessionResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readCookie(request, REFRESH_COOKIE);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cookie requis");
        }

        IssuedSession issued = authService.refresh(refreshToken);
        attachCookies(response, issued.accessToken(), issued.refreshToken(), issued.session());
        return ApiResponse.ok("Token rafraichi", issued.session());
    }

    @GetMapping("/me")
    @Operation(summary = "Session courante", description = "Retourne la session a partir du token (header Authorization ou cookie).")
    public ApiResponse<AuthResponseDTO.SessionResponse> me(
            @Parameter(description = "Bearer access token") 
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            HttpServletRequest request
    ) {
        String access = extractAccessToken(request, authorizationHeader);
        return ApiResponse.ok("Session active", authService.me(access));
    }

    @PostMapping("/logout")
    @Operation(summary = "Deconnexion", description = "Invalidate la session courante et efface les cookies.")
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = readCookie(request, REFRESH_COOKIE);
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.logout(refreshToken);
        }
        clearCookies(response);
        return ApiResponse.ok("Deconnexion reussie", null);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verifier OTP", description = "Valide le code OTP envoye par email et SMS.")
    public ApiResponse<Void> verifyOtp(@Valid @RequestBody AuthRequestDTO.VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ApiResponse.ok("Code OTP verifie", null);
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Renvoyer OTP", description = "Regenerer et renvoyer le code OTP au client.")
    public ApiResponse<Void> resendOtp(@Valid @RequestBody AuthRequestDTO.ResendOtpRequest request) {
        authService.resendOtp(request);
        return ApiResponse.ok("Code OTP renvoye", null);
    }

    private void attachCookies(HttpServletResponse response,
                               String accessToken,
                               String refreshToken,
                               AuthResponseDTO.SessionResponse session) {
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_COOKIE, accessToken)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(session.accessTokenExpiresIn())
                .sameSite(sameSiteForAuthCookie())
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(session.refreshTokenExpiresIn())
                .sameSite(sameSiteForAuthCookie())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void clearCookies(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(ACCESS_COOKIE, "")
                .path("/")
                .httpOnly(true)
                .secure(secureCookies)
                .maxAge(0)
                .sameSite(sameSiteForAuthCookie())
                .build().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(REFRESH_COOKIE, "")
                .path("/")
                .httpOnly(true)
                .secure(secureCookies)
                .maxAge(0)
                .sameSite(sameSiteForAuthCookie())
                .build().toString());
    }

    private String sameSiteForAuthCookie() {
        return secureCookies ? "None" : "Lax";
    }

    private String extractAccessToken(HttpServletRequest request, String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length()).trim();
        }

        String fromCookie = readCookie(request, ACCESS_COOKIE);
        if (fromCookie != null && !fromCookie.isBlank()) {
            return fromCookie;
        }

        throw new IllegalArgumentException("Access token requis");
    }

    private String readCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
