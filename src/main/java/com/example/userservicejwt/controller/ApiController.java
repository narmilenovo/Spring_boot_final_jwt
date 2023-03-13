package com.example.userservicejwt.controller;

import com.example.userservicejwt.dto.UserDto;
import com.example.userservicejwt.entity.Role;
import com.example.userservicejwt.entity.User;
import com.example.userservicejwt.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@SecurityRequirement(name = "javainuseapi")
public class ApiController {
    @Autowired
    private UserService userService;
    @GetMapping("/user")
    public ResponseEntity<User> getUser(@RequestParam String email)
    {
        return ResponseEntity.ok().body(userService.getUser(email));
    }
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers()
    {
        return ResponseEntity.ok().body(userService.getUsers());
    }
    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody UserDto userDto)
    {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(userDto));
    }
    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role)
    {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }
    @PostMapping("/role/addToUser")
    public ResponseEntity<?> addRoleToUser(@RequestParam String email,@RequestParam String name)
    {
        userService.addRoleToUser(email, name);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        userService.refreshToken(request,response);
    }

    //OPTIONAL API TO LOGIN TO DISPLAY ON SWAGGER, COMMENT IT IF U WANT
//    @CrossOrigin
//    @PostMapping("/login")
//    public AuthToken login(String email, String password)
//    {
//        return userService.login(email,password);
//    }
}
