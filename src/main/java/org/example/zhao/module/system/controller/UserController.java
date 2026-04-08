package org.example.zhao.module.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.system.dto.LoginReq;
import org.example.zhao.module.system.dto.LoginResp;
import org.example.zhao.module.system.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.example.zhao.module.system.dto.RegisterReq;
import org.example.zhao.module.system.entity.SysUser;
import org.example.zhao.security.SecurityUtil;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @PostMapping("/login")
    public R<LoginResp> login(@Validated @RequestBody LoginReq req) {
        return R.ok(authService.login(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterReq req) {
        authService.register(req.getUsername(), req.getPassword(), req.getRoleCode());
        return R.ok();
    }

    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody SysUser user) {
        Long userId = SecurityUtil.currentUserId();
        authService.updateProfile(userId, user.getUsername(), user.getRealName(), user.getPassword());
        return R.ok();
    }
}

