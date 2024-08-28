package com.firefly.sharecloud.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    @JsonIgnore
    private Integer id; // 主键ID


    private String userName; // 用户名

    @JsonIgnore
    private String password; //用户密码

    @Email
    private String email;

    private Integer allocated;

    @JsonIgnore
    private String phoneNumber;

//    @JsonIgnore
//    private LocalDateTime createTime;//创建时间
//
//    @JsonIgnore
//    private LocalDateTime updateTime;//更新时间
//
//    @JsonIgnore
//    private Boolean isDelete; // 是否删除该数据

}
