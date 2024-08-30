package com.firefly.sharemount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class LoginDTO {

    @NotNull
    private String userLoginName;
    @NotNull
    private String password;

}
