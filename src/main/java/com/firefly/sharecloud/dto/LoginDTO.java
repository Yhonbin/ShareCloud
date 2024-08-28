package com.firefly.sharecloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class LoginDTO {

    @NotNull
    private String userLoginName;
    @NotNull
    private String password;

    private String verification;
}
