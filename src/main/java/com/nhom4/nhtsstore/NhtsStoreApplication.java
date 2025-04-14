package com.nhom4.nhtsstore;

import com.nhom4.nhtsstore.services.UserService;
import com.nhom4.nhtsstore.ui.MainFrame;
import com.nhom4.nhtsstore.viewmodel.user.UserCreateVm;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@SpringBootApplication
public class NhtsStoreApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(NhtsStoreApplication.class)
                .headless(false)
                .run(args);

    }

    @Bean
    public CommandLineRunner startUI(MainFrame mainFrame,UserService userService, PasswordEncoder passwordEncoder) {

        return args -> {
            if (userService.findByUsername("phamduyhuy") == null) {
                // Create an admin user
                UserCreateVm adminUser = new UserCreateVm();
                adminUser.setUsername("phamduyhuy");
                adminUser.setPassword("admin123"); // Securely encode password
                adminUser.setEmail("admin@example.com");
                adminUser.setFullName("Administrator");
                adminUser.setRoles(Set.of("ROLE_SUPER_ADMIN"));
                adminUser.setPermissions(Set.of("FULL_ACCESS"));

                userService.createUser(adminUser);

                System.out.println("Admin user created successfully!");
            } else {
                System.out.println("Admin user already exists.");
            }


        };
    }
}
