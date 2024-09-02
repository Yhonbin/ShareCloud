package com.firefly.sharemount.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageDTO implements Serializable {

    BigInteger owner;

    String name;

    @NotNull
    Integer occupation;

    @NotNull
    Boolean readonly;

    @NotNull
    String storageInterface;
}
