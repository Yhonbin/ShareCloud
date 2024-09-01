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
public class Storage implements Serializable {
    @NotNull
    private BigInteger id;

    private BigInteger owner;

    private String name;

    private Integer occupation;

    private Boolean readonly;
}
