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
public class AuthUser {
    private Long id;

    private String username;

    private String slatPassword;

    private String salt;

    private String phone;

    private String mail;

    private Integer status;

    private Long groupId;

    private Date timeCreated;

    private Date timeUpdated;

    public AuthUser(Long id, String username, String password, String salt, String phone, String mail, Long groupId) {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.slatPassword = password;
        this.phone = phone;
        this.mail = mail;
        this.groupId = groupId;
    }
}