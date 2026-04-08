package org.example.zhao.module.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResp {
    private String token;
    private String tokenType;
    private Long userId;
    private String username;
    private String realName;
    private List<String> roles;
}

