package com.firefly.sharemount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull
    BigInteger id;

    String name;

    Boolean isGroup;
}
