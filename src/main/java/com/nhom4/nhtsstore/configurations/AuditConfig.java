package com.nhom4.nhtsstore.configurations;

import com.nhom4.nhtsstore.ui.ApplicationState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    private final ApplicationState applicationState;

    public AuditConfig(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            if (applicationState != null && applicationState.isAuthenticated() &&
                    applicationState.getCurrentUser() != null) {
                return Optional.of(applicationState.getCurrentUser().getUsername());
            }
            return Optional.of("system");
        };
    }
}
