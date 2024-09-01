package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @NotNull
    BigInteger id; // 主键

    String name; //用户名

    BigInteger root; // 根文件夹主键
}
