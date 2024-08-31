package com.firefly.sharemount.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions;

import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

@Component
public class ApplicationConfiguration {
    @Value("${pathToConfig}")
    private String configPath;

    private Path configFilePath;

    private Map<String, Object> config;
    // 读取外部配置文件
    public void loadConfig() {
        Yaml yaml = new Yaml();
        configFilePath = Paths.get(configPath,"config.yml");
        try (InputStream inputStream = Files.newInputStream(configFilePath.toFile().toPath())) {
            config = Collections.synchronizedMap(yaml.load(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取循环嵌套值
    public Object getNestedConfig(String configValue) {
        if (config == null) {
            return null;
        }
        String[] keys = configValue.split("\\.");
        // 遍历嵌套的键
        Object currentValue = config;
        for (String key : keys) {
            if (currentValue instanceof Map) {
                currentValue = ((Map<String, Object>) currentValue).get(key);
            } else {
                return null;
            }
        }
        return currentValue;
    }

    // 修改配置并保存
    public void updateNestedConfig(String key, Object value) {
        Yaml yaml;
        try {
            // 加载当前配置
            if (config != null) {
                String[] keys = key.split("\\.");
                // 更新嵌套的配置项
                updateNestedMap(config, keys, value);

                // 保存修改后的配置
                DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // 保持 YAML 格式
                yaml = new Yaml(options);

                try (FileWriter writer = new FileWriter(configFilePath.toFile())) {
                    yaml.dump(config, writer);
                }

                System.out.println("配置已更新并保存！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 递归更新嵌套的Map
    private void updateNestedMap(Map<String, Object> config, String[] keys, Object value) {
        Map<String, Object> currentMap = config;
        for (int i = 0; i < keys.length - 1; i++) {
            String key = keys[i];
            currentMap = (Map<String, Object>) currentMap.get(key); // 获取下一级的Map
        }
        currentMap.put(keys[keys.length - 1], value); // 更新最内层的键值对
    }
}
