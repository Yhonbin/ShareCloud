package com.firefly.sharemount.component;

public interface RedisTemplateComponent {

    void set(String key, String value);

    String get(String key);

    void setExpire(String key,long time);

    void remove(String key);

    Long increment(String key, long delta);
}
