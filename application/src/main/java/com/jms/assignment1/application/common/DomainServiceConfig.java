package com.jms.assignment1.application.common;

import com.jms.assignment1.service.CorrectRateCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public CorrectRateCalculator correctRateCalculator() {
        return new CorrectRateCalculator();
    }
}
