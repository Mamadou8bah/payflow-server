package com.mamadou.payflow.paymentlink.repository;

import com.mamadou.payflow.paymentlink.entity.PaymentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, Long> {
    Optional<PaymentLink> findByReferenceIgnoreCase(String reference);
    List<PaymentLink> findByMerchantIdOrderByIdDesc(Long merchantId);
    Optional<PaymentLink> findByIdAndMerchantId(Long id, Long merchantId);
}
