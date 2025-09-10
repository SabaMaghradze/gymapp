package com.rest.gymapp.config;

import com.rest.gymapp.utils.CredentialsGenerator;
import com.rest.gymapp.utils.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CredentialsGenerator credentialsGenerator() {
        return new CredentialsGenerator();
    }

    @Bean
    public Mappers mapper() {
        return new Mappers();
    }
}
