package com.jms.assignment1.infrastructure.config;

import com.jms.assignment1.repository.UserProblemHistoryRepository;
import com.jms.assignment1.service.CorrectRateCalculator;
import com.jms.assignment1.service.ProblemCorrectRateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public CorrectRateCalculator correctRateCalculator() {
        return new CorrectRateCalculator();
    }

    @Bean
    public ProblemCorrectRateService problemCorrectRateService(UserProblemHistoryRepository userProblemHistoryRepository,
                                                               CorrectRateCalculator correctRateCalculator) {
        return new ProblemCorrectRateService(userProblemHistoryRepository, correctRateCalculator);
    }
}
