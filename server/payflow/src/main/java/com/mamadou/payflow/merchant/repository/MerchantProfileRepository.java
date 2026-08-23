package com.mamadou.payflow.merchant.repository;

import com.mamadou.payflow.merchant.entity.MerchantProfile;
import com.mamadou.payflow.merchant.enums.MerchantVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantProfileRepository extends JpaRepository<MerchantProfile, Long> {
    Optional<MerchantProfile> findByUserId(Long userId);
    List<MerchantProfile> findByVerificationStatusOrderBySubmittedAtDesc(MerchantVerificationStatus status);
}
