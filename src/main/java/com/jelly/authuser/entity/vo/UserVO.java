package com.jelly.authuser.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * @author guodongzhang
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserVO {

    @JSONField(name = "id")
    private Long id;

    @JSONField(name = "username")
    private String username;

    @JSONField(name = "phone")
    private String phone;

    @JSONField(name = "mail")
    private String mail;

    @JSONField(name = "real_name")
    private String realName;

    @JSONField(name = "sex")
    private Integer sex;

    @JSONField(name = "birth")
    private Long birth;

    @JSONField(name = "avatar_url")
    private String avatarUrl;

    @JSONField(name = "group_name")
    private String groupName;

    @JSONField(name = "token")
    private String token;

}
