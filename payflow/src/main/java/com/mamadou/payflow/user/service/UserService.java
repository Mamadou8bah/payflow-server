package com.mamadou.payflow.user.service;

import com.mamadou.payflow.common.Exception.AccountNotFoundException;
import com.mamadou.payflow.user.dto.CreateUserRequest;
import com.mamadou.payflow.user.dto.UserProfileResponse;
import com.mamadou.payflow.user.dto.UserUpdateRequest;
import com.mamadou.payflow.user.enums.UserStatus;
import com.mamadou.payflow.user.mapper.UserMapper;
import com.mamadou.payflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserProfileResponse createProfile(CreateUserRequest request){
        var newUser= userMapper.mapToUser(request);
        newUser.setEnabled(false);
        newUser.setPhoneVerified(false);
        newUser.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(newUser);
        return userMapper.mapToUserProfileResponse(newUser);
    }

    @Transactional
    public UserProfileResponse userProfile(long id,UserUpdateRequest request){
        var user = userRepository.findById(id).orElseThrow(
                ()->new  AccountNotFoundException("This account does not exist")
        );
        if (request.firstName()!=null){
            user.setFirstName(request.firstName());
        }
        if (request.lastname()!=null){
            user.setLastName(request.lastname());
        }
        userRepository.save(user);
        return userMapper.mapToUserProfileResponse(user);
    }

    @Transactional
    public String lockUser(long id){
        var user = userRepository.findById(id).orElseThrow(
                ()->new AccountNotFoundException("This account does not exist")
        );
        user.setUserStatus(UserStatus.SUSPENDED);
        userRepository.save(user);
        return user.getFirstName()+" "+user.getLastName()+" Account has been locked";
    }


}
