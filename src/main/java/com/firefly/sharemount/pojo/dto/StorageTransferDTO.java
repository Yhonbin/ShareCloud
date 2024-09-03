package com.firefly.sharemount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageTransferDTO {

    private BigInteger srcId;

    @NotNull
    private BigInteger dstId;
    @NotNull
    private BigInteger storageId;
}
