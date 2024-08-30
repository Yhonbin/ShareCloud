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
    private SymbolicLink symbolicLink;
    private User linkOwner;
    private Queue<String> restPath;

    private boolean isVirtual() {
        return storage == null;
    }

    private boolean isSymbolicLink() {
        return symbolicLink != null;
    }
}
