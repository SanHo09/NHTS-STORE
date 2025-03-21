package com.nhom4.nhtsstore;


import com.nhom4.nhtsstore.entities.Permission;
import com.nhom4.nhtsstore.entities.Role;
import com.nhom4.nhtsstore.entities.User;
import com.nhom4.nhtsstore.repositories.UserRepository;
import com.nhom4.nhtsstore.ui.MainFrame;
import com.nhom4.nhtsstore.ui.login.LoginFrame;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import java.util.Set;

@SpringBootApplication
public class NhtsStoreApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(NhtsStoreApplication.class)
                .headless(false)
                .run(args);
    }
    @Bean
    public CommandLineRunner startUI(MainFrame mainFrame ,UserRepository userRepository, PasswordEncoder passwordEncoder) {
        if (userRepository.findByUsernameWithRolesAndPermissions("admin").isEmpty()) {
            // Create test admin user
            User testAdmin = new User();
            testAdmin.setUsername("admin");
            testAdmin.setPassword(passwordEncoder.encode("admin123"));
            testAdmin.setFullName("Test Administrator");
            testAdmin.setEmail("admin@example.com");
            // Create roles for test admin user
            Role role = new Role();
            role.setRoleName("ROLE_ADMIN");
            role.setDescription("Administrator role");
            Permission permission = new Permission();
            permission.setPermissionName("CREATE_USER");
            permission.setDescription("Create user permission");
            role.setPermissions(Set.of(permission));
            testAdmin.setRoles(Set.of(role));

            userRepository.save(testAdmin);
        }
        return args -> {
            // Show login frame first

        };
    }
}
