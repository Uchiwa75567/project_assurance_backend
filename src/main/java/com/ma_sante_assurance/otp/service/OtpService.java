package com.ma_sante_assurance.otp.service;

import com.ma_sante_assurance.otp.entity.OtpCode;
import com.ma_sante_assurance.otp.repository.OtpCodeRepository;
import com.ma_sante_assurance.common.enums.OtpType;
import com.ma_sante_assurance.common.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
public class OtpService {

    private final OtpCodeRepository otpRepository;
    private static final SecureRandom random = new SecureRandom();

    public OtpService(OtpCodeRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Transactional
    public String generateOtp(String userId, OtpType type) {
        // Delete old unused OTPs
        otpRepository.deleteByUserIdAndType(userId, type);

        // Generate 6-digit code
        String code = String.format("%06d", random.nextInt(1000000));

        OtpCode otp = OtpCode.builder()
                .id(IdGenerator.uuid())
                .userId(userId)
                .type(type)
                .code(code)
                .expiresAt(Instant.now().plusSeconds(300)) // 5 min
                .used(false)
                .build();

        otpRepository.save(otp);
        return code;
    }

    @Transactional(readOnly = true)
    public Optional<OtpCode> findValidOtp(String userId, OtpType type, String code) {
        return otpRepository.findByUserIdAndTypeAndCodeAndUsedFalseAndExpiresAtGreaterThan(userId, type, code, Instant.now());
    }

    @Transactional
    public boolean validateOtp(String userId, OtpType type, String code) {
        return otpRepository.findByUserIdAndTypeAndCodeAndUsedFalseAndExpiresAtGreaterThan(userId, type, code, Instant.now())
                .map(otp -> {
                    otp.setUsed(true);
                    otpRepository.save(otp);
                    return true;
                }).isPresent();
    }

    @Transactional
    public void invalidateUserOtps(String userId) {
        otpRepository.deleteByUserIdAndType(userId, OtpType.SMS);
        otpRepository.deleteByUserIdAndType(userId, OtpType.EMAIL);
    }
}

