package com.mamadou.payflow.merchant.repository;

import com.mamadou.payflow.merchant.entity.MerchantRegistrationSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRegistrationSessionRepository extends JpaRepository<MerchantRegistrationSession, String> {
    Optional<MerchantRegistrationSession> findByPhoneNumber(String phoneNumber);
}
