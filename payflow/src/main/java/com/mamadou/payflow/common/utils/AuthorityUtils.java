package com.mamadou.payflow.common.utils;

import com.mamadou.payflow.user.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

public class AuthorityUtils {

    public static Set<GrantedAuthority> getGrantedAuthorities(Role role) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(
                "ROLE_"+role.name()
        ));
        role.getPermissions().forEach(permission -> {
            authorities.add(new SimpleGrantedAuthority(permission.name()));
        });
        return authorities;
    }
}
