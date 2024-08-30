package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Queue;

@Data
@AllArgsConstructor
public class FileBO {
    private Storage storage;
    private VirtualFolder virtualFolder;
    private User vfOwner;
    private Queue<String> restPath;
    private SymbolicLink symbolicLink;

    private boolean isVirtual() {
        return storage == null;
    }

    private boolean isSymbolicLink() {
        return symbolicLink != null;
    }
}
