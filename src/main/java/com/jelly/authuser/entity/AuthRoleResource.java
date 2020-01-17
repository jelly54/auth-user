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
public class AuthRoleResource {
    private Integer id;

    private Integer roleId;

    private Integer resourceId;

    private Date timeCreated;

    private Date timeUpdated;

    public AuthRoleResource(Integer roleId, Integer resourceId) {
        this.roleId = roleId;
        this.resourceId = resourceId;
    }
}