package com.example.userservicejwt;

import com.example.userservicejwt.dto.UserDto;
import com.example.userservicejwt.entity.Role;
import com.example.userservicejwt.service.UserService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication

@SecurityScheme(name = "javainuseapi", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class UserServiceJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceJwtApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner runner(UserService userService)
    {
        return args -> {
          userService.saveRole(new Role(null,"ROLE_USER"));
          userService.saveRole(new Role(null,"ROLE_ADMIN"));
          userService.saveRole(new Role(null,"ROLE_SUPER_ADMIN"));

          userService.saveUser(new UserDto(null,"imran","imran@gmail.com","12345678","12345678",new ArrayList<>()));
          userService.saveUser(new UserDto(null,"narmi","narmi@gmail.com","12345678","12345678",new ArrayList<>()));
          userService.saveUser(new UserDto(null,"nemo","nemo@gmail.com","12345678","12345678",new ArrayList<>()));

          userService.addRoleToUser("imran@gmail.com","ROLE_USER");
          userService.addRoleToUser("narmi@gmail.com","ROLE_ADMIN");
          userService.addRoleToUser("nemo@gmail.com","ROLE_SUPER_ADMIN");

        };
    }
}
