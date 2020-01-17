package com.jelly.authuser.entity;

import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.exception.ServerException;
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
public class AuthRole {
    private Integer id;

    private String roleName;

    private Integer status;

    private Date timeCreated;

    private Date timeUpdated;

    public AuthRole(String roleName) {
        String rolePrefix = "role_";
        if (roleName == null || !roleName.startsWith(rolePrefix)) {
            throw new ServerException(ErrorEnum.INVALID_PARAMS);
        }
        this.roleName = roleName;
    }
}