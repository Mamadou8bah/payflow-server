package com.mamadou.payflow.auth.service;

import com.mamadou.payflow.auth.dto.*;
import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.common.Exception.InvalidPasswordException;
import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final SCryptPasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    @Transactional
    public RegisterResponse registerUser(RegisterRequestStage2 registerRequest) {
        var user= userRepository.findByPhone(registerRequest.phoneNumber());
        User newUser;

        if(!user.isPresent()){
            newUser = User.builder()
                    .phoneNumber(registerRequest.phoneNumber())
                    .registrationStage(1)
                    .build();

        }else
            newUser = user.get();
        newUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(newUser);
        //I will implement the logic to generate OTP here
        return new RegisterResponse();
    }
    public RegisterResponse registerUser2(RegisterRequestStage3 registerRequest) {
        var user= userRepository.findByPhone(registerRequest.phoneNumber());
        if(!user.isPresent()){
            throw new AccountNotFoundException("User not found");
        }
        if(!passwordEncoder.matches(registerRequest.password(),user.get().getPassword())){
            throw new InvalidPasswordException("Incorrect Code");
        }
        String refreshToken= refreshTokenService.generateRefreshToken(user.get());
        String accessToken = refreshTokenService.refreshAccessToken(refreshToken);

        return new RegisterResponse(

        );

    }


    @Transactional
    public RegisterResponse registerMerchant(RegisterRequest registerRequest) {
        return new RegisterResponse();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public LoginResponse merchantLogin(LoginRequest request){
        return new LoginResponse();
    }


}
