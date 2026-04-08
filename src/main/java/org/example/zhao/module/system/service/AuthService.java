package org.example.zhao.module.system.service;

import org.example.zhao.module.system.dto.LoginResp;

public interface AuthService {
    LoginResp login(String username, String password);
    void register(String username, String password, String roleCode);
    void updateProfile(Long userId, String username, String realName, String password);
}

