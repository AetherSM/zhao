package org.example.zhao.module.system.controller;

import lombok.RequiredArgsConstructor;
import org.example.zhao.common.api.R;
import org.example.zhao.module.system.dto.LoginReq;
import org.example.zhao.module.system.dto.LoginResp;
import org.example.zhao.module.system.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.example.zhao.module.system.dto.RegisterReq;

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
}

