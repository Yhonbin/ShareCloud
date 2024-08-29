package com.firefly.sharemount.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class RegisterDTO {

    @ApiModelProperty(value = "用户名",required = true,example = "root")
    @NotNull(message = "用户名不能为空")
    @Pattern(regexp = "^[0-9a-zA-Z_\\u4e00-\\u9fa5]+$",message = "用户名只能由汉字、数字、英文字母、_组成")
    @Length(min = 3, max = 25)
    private String username;

    @ApiModelProperty(value = "验证方式", required = true, example = "asbc@qq.com")
    @NotNull(message = "验证方式不能为空")
    private String verifyWay;

    @NotNull(message = "验证码不能为空")
    @Length(min = 6, max = 6, message = "验证码长度为6位")
    private String verification;

    @ApiModelProperty(value = "密码", required = true, example = "toor")
    @Pattern(regexp = "^[0-9a-zA-Z_]+$",message = "密码只能由汉字、数字、英文字母或下划线组成")
    @Length(min = 5, max = 16)
    private String password;


}
