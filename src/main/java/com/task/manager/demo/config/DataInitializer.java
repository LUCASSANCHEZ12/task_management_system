package com.task.manager.demo.config;

import com.task.manager.demo.entity.Role;
import com.task.manager.demo.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(Role.builder().name("USER").build());
            }
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(Role.builder().name("ADMIN").build());
            }
        };
    }
}
