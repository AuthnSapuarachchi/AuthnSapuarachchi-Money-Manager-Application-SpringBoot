package com.authcodelab.smartmoneymanageapp.repository;

import com.authcodelab.smartmoneymanageapp.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // Find a profile by email
    Optional<ProfileEntity> findByEmail(String email);

    // Find a profile by activation token
    Optional<ProfileEntity> findByActivationToken(String activationToken);
}
