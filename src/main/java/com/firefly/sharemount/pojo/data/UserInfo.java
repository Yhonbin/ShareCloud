package com.firefly.sharemount.pojo.data;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    @NotNull
    @JsonIgnore
    private BigInteger id; // 主键ID

    private BigInteger userId; // user表id

    @JsonIgnore
    private String password; //用户密码

    @Email
    private String email;

    @JsonIgnore
    private String phoneNumber;

    private Integer allocated;



//    @JsonIgnore
//    private LocalDateTime createTime;//创建时间
//
//    @JsonIgnore
//    private LocalDateTime updateTime;//更新时间
//
//    @JsonIgnore
//    private Boolean isDelete; // 是否删除该数据

}
