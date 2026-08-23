package com.mamadou.payflow.common.security;

import com.mamadou.payflow.user.entity.User;
import com.mamadou.payflow.user.enums.Role;

public final class SecurityRoleUtils {

    private SecurityRoleUtils() {}

    public static boolean isAgent(User user) {
        return user != null && user.getRole() == Role.AGENT;
    }

    public static boolean isMerchant(User user) {
        return user != null && user.getRole() == Role.MERCHANT;
    }

    public static boolean isDeveloper(User user) {
        return user != null && user.getRole() == Role.DEVELOPER;
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }
}
