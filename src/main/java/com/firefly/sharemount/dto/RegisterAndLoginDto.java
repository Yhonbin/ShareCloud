package com.firefly.sharemount.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAndLoginDto {
    @ApiModelProperty(value = "用户名",required = true,example = "root")

    private String username;
    @ApiModelProperty(value = "密码", required = true, example = "toor")
    private String password;

    @ApiModelProperty(value = "电子邮箱", required = true, example = "asbc@qq.com")
    @Email
    private String email;

}


