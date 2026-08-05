package com.saranaresturantsystem.repository.users;

import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.entities.users.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByTokenAndType(String token, String type);
    Optional<VerificationToken> findByUserAndType(User user, String type);
    void deleteByUserAndType(User user, String type);
}
