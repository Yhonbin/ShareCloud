package com.firefly.sharemount;

import com.firefly.sharemount.config.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@SpringBootApplication
@EnableScheduling
public class ShareMountApplication {


    @Value("${pathToConfig}")
    private String pathToConfig;

    public static void main(String[] args) {
        SpringApplication.run(ShareMountApplication.class, args);
    }



    @Bean
    public CommandLineRunner checkAndLoadConfig() {
        return args -> {
            // 构建外部配置文件的路径
            Path configFilePath = Paths.get(pathToConfig, "config.yml");
            if (!Files.exists(configFilePath)) {
                // 从 resources 目录中读取默认的 config.yml
                ClassPathResource resource = new ClassPathResource("default-config.yml");
                try (InputStream inputStream = resource.getInputStream()) {

                    // 创建父目录（如果不存在）
                    if (!Files.exists(configFilePath.getParent())) {
                        Files.createDirectories(configFilePath.getParent());
                    }
                    // 将资源文件内容写入到外部的 config.yml 文件中
                    Files.copy(inputStream, configFilePath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("默认配置已写入到 " + configFilePath);
                } catch (IOException e) {
                    System.err.println("无法读取默认配置文件或写入外部配置文件: " + e.getMessage());
                    throw e;
                }
            }
        };
    }

}
