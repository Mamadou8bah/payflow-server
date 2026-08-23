package com.mamadou.payflow.auth.repository;

import com.mamadou.payflow.auth.entity.APIKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface APIKeyRepository extends JpaRepository<APIKey, Long> {
    Optional<APIKey> findByName(String name);
    Optional<APIKey> findByPublicId(String publicId);
    List<APIKey> findByUserId(Long userId);
}
