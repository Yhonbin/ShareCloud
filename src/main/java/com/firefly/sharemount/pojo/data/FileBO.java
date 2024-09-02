package com.firefly.sharemount.pojo.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Deque;

@Data
@AllArgsConstructor
public class FileBO implements Serializable {
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileBO(");
        if (storage != null) {
            sb.append(storage.getId());
            for (String s : storageRestPath) {
                sb.append('/');
                sb.append(s);
            }
        } else sb.append("null");
        sb.append(", ");
        if (virtualFolder != null) {
            sb.append(virtualFolder.getName());
            sb.append('[');
            sb.append(vfOwner.getId());
            sb.append("->");
            sb.append(virtualFolder.getId());
            sb.append(']');
            for (String s : vfRestPath) {
                sb.append('/');
                sb.append(s);
            }
        } else sb.append("null");
        sb.append(", ");
        if (symbolicLink != null) {
            sb.append(symbolicLink.getName());
            sb.append('[');
            sb.append(linkOwner.getId());
            sb.append("->");
            sb.append(symbolicLink.getId());
            sb.append(']');
        } else sb.append("null");
        sb.append(')');
        return sb.toString();
    }
}
