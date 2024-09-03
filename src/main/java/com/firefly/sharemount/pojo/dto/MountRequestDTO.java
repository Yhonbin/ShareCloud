package com.firefly.sharemount.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MountRequestDTO {
    @ApiModelProperty(value = "请求路径", required = true, example = "aaa/bbb")
    @Pattern(regexp = "^[^\\\\:*?\"<>|]+$", message = "路径包含非法字符")
    String path;

    BigInteger root;

    BigInteger storage;
}
