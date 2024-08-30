package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageConnectionLog {
    @NotNull
    private BigInteger id;

    private Date updateTime;

    private Boolean success;

    private String log;
}
