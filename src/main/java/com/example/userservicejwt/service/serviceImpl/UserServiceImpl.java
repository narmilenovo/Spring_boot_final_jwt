package com.example.userservicejwt.service.serviceImpl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.userservicejwt.dto.AuthToken;
import com.example.userservicejwt.dto.UserDto;
import com.example.userservicejwt.entity.Role;
import com.example.userservicejwt.entity.User;
import com.example.userservicejwt.repository.RoleRepository;
import com.example.userservicejwt.repository.UserRepository;
import com.example.userservicejwt.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public User saveUser(UserDto userDto) {
        User user = new User();
        if (userDto.getPassword().equals(userDto.getConfirmPassword()))
        {
            user.setId(userDto.getId());
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            user.setRoles(userDto.getRoles());
            return userRepository.save(user);
        }
        else{
            throw new RuntimeException("User Not Saved");
        }

    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String email, String name) {
        User user = userRepository.findByEmail(email);
        Role role = roleRepository.findByName(name);
        user.getRoles().add(role);

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            try{
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = this.getUser(username);
                String access_token = JWT.create().withSubject(user.getEmail()).withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)).withIssuer(request.getRequestURL().toString()).withClaim("roles",user.getRoles().stream().map(Role::getName).collect(Collectors.toList())).sign(algorithm);
                Map<String,String> tokens = new HashMap<>();
                tokens.put("access_token",access_token);
                tokens.put("refresh_token",refresh_token);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);
            }
            catch (Exception e)
            {
                response.setHeader("error",e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                Map<String,String> error = new HashMap<>();
                error.put("error_message",e.getMessage());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(),error);
            }
        }
        else {
            throw new RuntimeException("Refresh Token is missing");
        }
    }

    @Override
    public AuthToken login(String email, String password) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email,password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
        User user = this.getUser(String.valueOf(authentication));
        String access_token = JWT.create().withSubject(user.getEmail()).withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)).withClaim("roles",user.getRoles().stream().map(Role::getName).collect(Collectors.toList())).sign(algorithm);
        String refresh_token = JWT.create().withSubject(user.getEmail()).withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)).sign(algorithm);
//        response.setHeader("access_token",access_token);
        return new AuthToken(access_token,refresh_token);

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null)
        {
            throw new UsernameNotFoundException("User Not Found");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),authorities);

    }
}
