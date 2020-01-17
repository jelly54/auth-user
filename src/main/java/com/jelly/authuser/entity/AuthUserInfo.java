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
public class AuthUserInfo {
    private Integer id;

    private Long userId;

    private String realName;

    private Integer sex;

    private Long birth;

    private String avatarUrl;

    private Date timeCreated;

    private Date timeUpdated;

    public AuthUserInfo(Long userId, String realName, Integer sex, Long birth, String avatarUrl) {
        this.userId = userId;
        this.realName = realName;
        this.sex = sex;
        this.birth = birth;
        this.avatarUrl = avatarUrl;
    }
}