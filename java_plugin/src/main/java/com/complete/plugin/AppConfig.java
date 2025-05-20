package com.complete.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public SysMLModelService sysMLModelService() {
        return new SysMLModelService();
    }
}
