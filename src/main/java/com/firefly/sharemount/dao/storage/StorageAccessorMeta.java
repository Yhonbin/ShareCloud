package com.firefly.sharemount.dao.storage;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface StorageAccessorMeta {
    String displayTypeName();
    String[] acceptInterfaceType();
    boolean allowMultipartUpload();
}
