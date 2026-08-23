package com.mamadou.payflow.merchant.entity;

import com.mamadou.payflow.merchant.enums.GambiaRegion;
import com.mamadou.payflow.merchant.enums.MerchantBusinessCategory;
import com.mamadou.payflow.merchant.enums.MerchantIdType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_registration_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantRegistrationSession {

    @Id
    @Column(length = 36)
    private String token;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    private boolean phoneVerified;

    private String otpHash;

    private LocalDateTime otpExpiresAt;

    private int stage;

    private String businessName;
    private String tradingName;

    @Enumerated(EnumType.STRING)
    private MerchantBusinessCategory category;

    @Enumerated(EnumType.STRING)
    private GambiaRegion region;

    private String cityOrArea;
    private String streetAddress;
    private String businessRegistrationNumber;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private MerchantIdType ownerIdType;

    private String ownerIdNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
