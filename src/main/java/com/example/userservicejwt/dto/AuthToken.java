package com.example.userservicejwt.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {

    private String access_token;

   private String refresh_token;


}