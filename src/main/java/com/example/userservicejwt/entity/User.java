package com.example.userservicejwt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @JsonProperty(access =  JsonProperty.Access.WRITE_ONLY)
    private String password;
    @ManyToMany(fetch=FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();
}
