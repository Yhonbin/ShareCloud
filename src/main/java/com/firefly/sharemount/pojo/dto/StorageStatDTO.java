package com.firefly.sharemount.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageStatDTO {
    @NotNull
    BigInteger id;

    UserDTO owner;

    String name;

    Boolean readonly;

    String type;

    String connectionInfo;
}
