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
public class AuthUserRole {
    private Integer id;

    private Long userId;

    private Integer roleId;

    private Date timeCreated;

    private Date timeUpdated;

    public AuthUserRole(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}