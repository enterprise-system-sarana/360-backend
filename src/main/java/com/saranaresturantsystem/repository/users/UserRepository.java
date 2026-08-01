package com.saranaresturantsystem.repository.users;

import com.saranaresturantsystem.entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Page<User> findByDeletedAtIsNull(Pageable pageable);
    Page<User> findByStoreIdAndDeletedAtIsNull(Long storeId, Pageable pageable);
}
