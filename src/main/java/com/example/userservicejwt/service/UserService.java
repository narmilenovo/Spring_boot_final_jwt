package com.example.userservicejwt.service;

import com.example.userservicejwt.dto.AuthToken;
import com.example.userservicejwt.dto.UserDto;
import com.example.userservicejwt.entity.Role;
import com.example.userservicejwt.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UserService {
    User saveUser(UserDto userDto);
    Role saveRole(Role role);
    void addRoleToUser(String email, String name);
    User getUser(String email);
    List<User> getUsers();

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    AuthToken login(String email, String password);
}
