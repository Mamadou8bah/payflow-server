package com.mamadou.payflow.merchant.entity;

import com.mamadou.payflow.merchant.enums.GambiaRegion;
import com.mamadou.payflow.merchant.enums.MerchantBusinessCategory;
import com.mamadou.payflow.merchant.enums.MerchantIdType;
import com.mamadou.payflow.merchant.enums.MerchantVerificationStatus;
import com.mamadou.payflow.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String businessName;

    private String tradingName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private MerchantBusinessCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GambiaRegion region;

    @Column(nullable = false, length = 80)
    private String cityOrArea;

    @Column(nullable = false, length = 255)
    private String streetAddress;

    private String businessRegistrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MerchantIdType ownerIdType;

    @Column(nullable = false, length = 40)
    private String ownerIdNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MerchantVerificationStatus verificationStatus;

    private String rejectionReason;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    @PrePersist
    public void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
        if (verificationStatus == null) {
            verificationStatus = MerchantVerificationStatus.PENDING_REVIEW;
        }
    }
}
