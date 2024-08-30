package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymbolicLink {
    @NotNull
    private BigInteger id;

    private BigInteger parent;

    private String name;

    private BigInteger targetUser;
}
