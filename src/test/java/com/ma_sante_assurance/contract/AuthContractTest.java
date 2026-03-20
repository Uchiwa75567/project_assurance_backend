package com.ma_sante_assurance.contract;

import com.ma_sante_assurance.auth.dto.AuthResponseDTO;
import com.ma_sante_assurance.common.enums.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AuthContractTest {

    @Test
    void sessionResponseShouldExposeExpectedFields() {
        AuthResponseDTO.SessionResponse dto = new AuthResponseDTO.SessionResponse(
                900,
                1209600,
                "user-id",
                "User Name",
                "user@mail.com",
                UserRole.CLIENT
        );

        Assertions.assertTrue(dto.accessTokenExpiresIn() > 0);
        Assertions.assertTrue(dto.refreshTokenExpiresIn() > 0);
        Assertions.assertEquals("user-id", dto.userId());
        Assertions.assertEquals("User Name", dto.fullName());
        Assertions.assertEquals("user@mail.com", dto.email());
        Assertions.assertEquals(UserRole.CLIENT, dto.role());
    }
}
