package com.ma_sante_assurance.otp.repository;

import com.ma_sante_assurance.otp.entity.OtpCode;
import com.ma_sante_assurance.common.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, String> {

    @Query("SELECT o FROM OtpCode o WHERE o.userId = :userId AND o.type = :type AND o.code = :code AND o.used = false AND o.expiresAt > :now")
    Optional<OtpCode> findByUserIdAndTypeAndCodeAndUsedFalseAndExpiresAtGreaterThan(@Param("userId") String userId, @Param("type") OtpType type, @Param("code") String code, @Param("now") Instant now);
    
    @Query("SELECT o FROM OtpCode o WHERE o.userId = :userId AND o.type = :type AND o.used = false ORDER BY o.createdAt DESC")
    Optional<OtpCode> findFirstByUserIdAndTypeAndUsedFalseOrderByCreatedAtDesc(@Param("userId") String userId, @Param("type") OtpType type);
    
    void deleteByUserIdAndType(String userId, OtpType type);
    
    boolean existsByUserIdAndTypeAndUsedFalse(String userId, OtpType type);
}

