package com.mamadou.payflow.user.entity;

import com.mamadou.payflow.auth.entity.RefreshToken;
import com.mamadou.payflow.common.utils.AuthorityUtils;
import com.mamadou.payflow.user.enums.Role;
import com.mamadou.payflow.user.enums.UserStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Table(name="users")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(
            nullable=false
    )
    private String firstName;
    @Column(
            nullable=false
    )
    private String lastName;

    private String middleName;

    @Column(
            nullable=false,
            length = 20,
            unique = true
    )
    private String phoneNumber;

    @NotNull
    private String passwordHash;

    @Email
    private String email;

    private boolean enabled;

    private int registrationStage;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private UserStatus userStatus;

    private boolean phoneVerified;

    private LocalDateTime lastLoginAt;

    private LocalDateTime lockedAt;


    private Integer failedLoginAttempts;

    @Column(nullable = false)
    private boolean twoFactorEnabled;

    private String twoFactorCodeHash;

    private String twoFactorChallengeId;

    private LocalDateTime twoFactorCodeExpiresAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.getGrantedAuthorities(role);
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return role==Role.MERCHANT? email:phoneNumber;
    }

    @Override
    public boolean isAccountNonLocked() {
        return lockedAt == null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
