package com.jelly.authuser.entity.bo;

import lombok.*;

import java.io.Serializable;

/**
 * 账户
 *
 * @author guodongzhang
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appId;
    private String password;
    private String salt;

}
