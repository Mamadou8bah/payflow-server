package com.mamadou.payflow.deposit;

import com.mamadou.payflow.deposit.dto.CreateDepositRequest;
import com.mamadou.payflow.deposit.entity.Deposit;
import com.mamadou.payflow.deposit.repository.DepositRepository;
import com.mamadou.payflow.deposit.service.DepositService;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import com.mamadou.payflow.wallet.entity.Wallet;
import com.mamadou.payflow.wallet.repository.WalletRepository;
import com.mamadou.payflow.webhook.client.CreateChargeResponse;
import com.mamadou.payflow.webhook.client.ModemPayClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DepositIdempotencyIntegrationTest {

    @Autowired
    DepositService depositService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    DepositRepository depositRepository;

    @MockBean
    ModemPayClient modemPayClient;

    @Test
    void idempotencyPreventsDuplicateCharge() {
        User user = userRepository.save(User.builder()
            .firstName("Test")
            .lastName("User")
            .phoneNumber("+100000000")
            .passwordHash("x")
            .email("t@example.com")
            .enabled(true)
            .userStatus(com.mamadou.payflow.user.enums.UserStatus.ACTIVE)
            .role(com.mamadou.payflow.user.enums.Role.USER)
            .twoFactorEnabled(false)
            .build());

        Wallet w = walletRepository.save(Wallet.builder().user(user).currency("USD").name("Primary").build());

        CreateChargeResponse resp = new CreateChargeResponse();
        resp.setId("ch_123");
        resp.setPaymentUrl("https://pay.example/123");

        when(modemPayClient.createCharge(any())).thenReturn(resp);

        CreateDepositRequest req = CreateDepositRequest.builder()
                .walletId(w.getId())
                .amount(new BigDecimal("10.00"))
                .currency("USD")
                .paymentMethod("mobile_money")
                .phoneNumber("+100000000")
                .idempotencyKey("idem-abc-123")
                .build();

        Deposit d1 = depositService.createDeposit(req, user.getId(), false);
        Deposit d2 = depositService.createDeposit(req, user.getId(), false);

        // service should return the same deposit for same idempotency key
        assertThat(d1.getId()).isEqualTo(d2.getId());
        assertThat(d1.getExternalPaymentId()).isEqualTo("ch_123");
        verify(modemPayClient, times(1)).createCharge(any());
    }
}
