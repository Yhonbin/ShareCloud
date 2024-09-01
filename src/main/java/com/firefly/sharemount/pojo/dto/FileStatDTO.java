package com.firefly.sharemount.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileStatDTO {
    private String name;

    private String type;  // file, dir, vdir, link, nonexistent

    private Long size;

    private Date lastModified;

    private StorageStatDTO mount;

    private String linkTarget;

    private UserDTO linkTargetUser;

    private FileStatDTO linkTargetStat;
}
