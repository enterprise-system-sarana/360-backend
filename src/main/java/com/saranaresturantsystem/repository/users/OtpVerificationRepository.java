package com.saranaresturantsystem.repository.users;

import com.saranaresturantsystem.entities.users.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findByEmailAndOtpCode(String email , String optCode);
    void deleteByEmail (String email);
}
