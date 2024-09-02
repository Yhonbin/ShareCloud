package com.firefly.sharemount.component;

import java.util.concurrent.TimeUnit;

public interface RedisTemplateComponent {

    void set(String key, String value);

    String get(String key);

    void setExpire(String key, long time, TimeUnit t);

    void remove(String key);

    Long increment(String key, long delta);
}
