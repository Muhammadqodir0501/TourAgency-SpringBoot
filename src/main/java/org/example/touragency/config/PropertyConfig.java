package org.example.touragency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import static org.example.touragency.constant.QueryConstants.PROPERTIES;

@Configuration
public class PropertyConfig {
    public PropertyConfig(){}

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer =
                new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource(PROPERTIES));
        configurer.setIgnoreUnresolvablePlaceholders(false);
        return configurer;
    }
}
