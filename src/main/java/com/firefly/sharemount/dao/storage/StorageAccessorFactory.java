package com.firefly.sharemount.dao.storage;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;

@Component
public class StorageAccessorFactory {
    private static final String RESOURCE_PATTERN = "/*.class";
    private static final String IMPL_SCAN_PACKAGE = "com.firefly.sharemount.dao.storage.impl";

    @AllArgsConstructor
    private static class AccessorMetadata {
        @Getter private StorageAccessorMeta annotation;
        @Getter private Method createNew;
        @Getter private Method getConnectionInfo;
    }

    private final HashMap<String, AccessorMetadata> accessors = new HashMap<>();

    @PostConstruct
    private void loadStorageAccessorImpls() {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    ClassUtils.convertClassNameToResourcePath(IMPL_SCAN_PACKAGE) + RESOURCE_PATTERN;
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory refractory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                MetadataReader reader = refractory.getMetadataReader(resource);
                String classname = reader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(classname);
                if (!clazz.isAnnotationPresent(StorageAccessorMeta.class)) continue;
                if (!isClassImplementedAccessorInterface(clazz)) continue;
                Method createNewMethod = getCreateNewMethod(clazz);
                if (createNewMethod == null) continue;
                Method getConnectionInfoMethod = getGetConnectionInfoMethod(clazz);
                if (getConnectionInfoMethod == null) continue;
                StorageAccessorMeta annotation = clazz.getAnnotation(StorageAccessorMeta.class);
                AccessorMetadata metadata = new AccessorMetadata(annotation, createNewMethod, getConnectionInfoMethod);
                for (String accept : annotation.acceptInterfaceType()) accessors.put(accept, metadata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isClassImplementedAccessorInterface(Class<?> clazz) {
        for (Class<?> i : clazz.getInterfaces()) {
            if (i.equals(StorageAccessor.class)) return true;
        }
        return false;
    }

    private Method getCreateNewMethod(Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            if ("createNew".equals(m.getName())) {
                if (!clazz.equals(m.getReturnType())) continue;
                if (m.getParameterCount() != 2) continue;
                Class<?>[] pts = m.getParameterTypes();
                if (!Boolean.class.equals(pts[0]) && !boolean.class.equals(pts[0])) continue;
                if (!JSONObject.class.equals(pts[1])) continue;
                return m;
            }
        }
        return null;
    }

    private Method getGetConnectionInfoMethod(Class<?> clazz) {
        for (Method m : clazz.getMethods()) {
            if ("getConnectionInfo".equals(m.getName())) {
                if (!String.class.equals(m.getReturnType())) continue;
                if (m.getParameterCount() != 1) continue;
                Class<?>[] pts = m.getParameterTypes();
                if (!JSONObject.class.equals(pts[0])) continue;
                return m;
            }
        }
        return null;
    }

    @SneakyThrows
    public StorageAccessor makeConnection(boolean readonly, JSONObject args) {
        String type = args.getString("type");
        if (!accessors.containsKey(type)) return null;
        return (StorageAccessor) accessors.get(type).getCreateNew().invoke(readonly, args);
    }

    public String getType(JSONObject args) {
        String type = args.getString("type");
        if (!accessors.containsKey(type)) return null;
        return accessors.get(type).getAnnotation().displayTypeName();
    }

    @SneakyThrows
    public String getConnectionInfo(JSONObject args) {
        String type = args.getString("type");
        if (!accessors.containsKey(type)) return null;
        return (String) accessors.get(type).getGetConnectionInfo().invoke(args);
    }

    public boolean isAllowMultipartUpload(JSONObject args) {
        String type = args.getString("type");
        if (!accessors.containsKey(type)) return false;
        return accessors.get(type).getAnnotation().allowMultipartUpload();
    }
}
