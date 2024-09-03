package com.firefly.sharemount.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StorageListResponseDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StorageListOfOneUserDTO {
        UserDTO user;
        List<StorageStatDTO> storages;
    }

    List<StorageListOfOneUserDTO> resp;
}
