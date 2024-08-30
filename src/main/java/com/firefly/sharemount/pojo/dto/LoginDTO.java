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
    @Pattern(regexp = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]+$",message = "用户名只能由汉字、数字、英文字母、_组成")
    private String userLoginName;
    @NotNull
    private String password;

}
