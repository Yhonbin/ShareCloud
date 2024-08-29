package com.firefly.sharemount.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    BigInteger id; // 主键

    String name; //用户名
}
