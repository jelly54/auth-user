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
public class AuthResource {
    private Integer id;

    private String resName;

    private Integer resType;

    private Integer parentId;

    private String url;

    private String method;

    private Integer status;

    private Date timeCreated;

    private Date timeUpdated;

    public AuthResource(Integer id, String resName, Integer resType, Integer parentId, String url, String method) {
        this.id = id;
        this.resName = resName;
        this.resType = resType;
        this.parentId = parentId;
        this.url = url;
        this.method = method;
    }
}