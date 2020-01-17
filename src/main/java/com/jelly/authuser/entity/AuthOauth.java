package com.jelly.authuser.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author guodongzhang
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AuthOauth {
    private Integer id;

    private Long userId;

    private String oauthType;

    private String oauthId;

    private String unionId;

    private String credential;

    private String refreshToken;

    private Date timeCreated;

    private Date timeUpdated;
}