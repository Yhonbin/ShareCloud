package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VirtualFolder {
    @NotNull
    private BigInteger id;

    private String name;

    private BigInteger parent;
}
