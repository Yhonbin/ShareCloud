package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Deque;

@Data
@AllArgsConstructor
public class FileBO {
    public static FileBO makeNewInvalidSymbolicLink(SymbolicLink symbolicLink, User owner) {
        return new FileBO(null, null, null, null, null, symbolicLink, owner);
    }

    private Storage storage;
    private Deque<String> storageRestPath;
    private VirtualFolder virtualFolder;
    private Deque<String> vfRestPath;
    private User vfOwner;
    private SymbolicLink symbolicLink;
    private User linkOwner;

    private boolean isVirtual() {
        return storage == null;
    }

    private boolean isSymbolicLink() {
        return symbolicLink != null;
    }
}
