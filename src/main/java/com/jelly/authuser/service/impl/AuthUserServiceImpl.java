package com.jelly.authuser.service.impl;

import com.jelly.authuser.dao.AuthUserDao;
import com.jelly.authuser.dao.AuthUserInfoDao;
import com.jelly.authuser.dao.AuthUserRoleDao;
import com.jelly.authuser.entity.AuthUser;
import com.jelly.authuser.entity.AuthUserInfo;
import com.jelly.authuser.entity.AuthUserRole;
import com.jelly.authuser.entity.bo.Account;
import com.jelly.authuser.exception.ErrorEnum;
import com.jelly.authuser.exception.ServerException;
import com.jelly.authuser.util.id.IdWorker;
import com.jelly.authuser.service.AuthUserService;
import com.jelly.authuser.util.CheckTools;
import com.jelly.authuser.util.CommonUtil;
import com.jelly.authuser.util.JsonWebTokenUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author guodongzhang
 */
@Service
public class AuthUserServiceImpl implements AuthUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthUserServiceImpl.class);

    @Autowired
    IdWorker idWorker;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    AuthUserDao userDao;

    @Resource
    AuthUserInfoDao userInfoDao;

    @Resource
    AuthUserRoleDao userRoleDao;

    @Value("${shiro.login.token-timeout}")
    Long tokenTimeout = 18000L;

    @Value("${shiro.login.jwt-timeout}")
    Long jwtTimeout = 864000L;


    private String[] generatePassword(String originPassword) {
        String salt = CommonUtil.getRandomString(6);
        String hashPass = new Md5Hash(originPassword, salt, 3).toString();
        return new String[]{hashPass, salt};
    }

    @Override
    public boolean existsUserInfoByUserId(Long userId) {
        int count = userInfoDao.countByUserId(userId);
        LOG.info("Count userInfo by userId. {}.", count);

        return count > 0;
    }

    @Override
    public boolean createUser(String username, String password, String phone, String mail, Long groupId) {
        AuthUser preSave = new AuthUser(null, username, null, null, phone, mail, groupId);

        long id = idWorker.nextId();
        preSave.setId(id);
        String[] saltPassword = generatePassword(password);
        preSave.setSlatPassword(saltPassword[0]);
        preSave.setSalt(saltPassword[1]);
        LOG.info("User will be saved. {}.", preSave);

        int insert = userDao.insert(preSave);
        LOG.info("Create user :{}.", insert > 0);
        return insert > 0;
    }

    @Override
    public boolean updateUserById(Long id, String username, String password, String salt, String phone, String mail, Long groupId) {
        AuthUser user = new AuthUser(id, username, password, salt, phone, mail, groupId);

        if (!StringUtils.isEmpty(user.getSlatPassword())) {
            LOG.info("User will change password. {}.", user);
            String[] saltPassword = generatePassword(user.getSlatPassword());
            user.setSlatPassword(saltPassword[0]);
            user.setSalt(saltPassword[1]);
        }
        LOG.info("User will be update. {}.", user);

        int update = userDao.updateByPrimaryKey(user);

        LOG.info("Update user :{}.", update > 0);
        return update > 0;
    }

    @Override
    public boolean createUserInfo(Long userId, String realName, Integer sex, Long birth, String avatarUrl) {
        AuthUserInfo userInfo = new AuthUserInfo(userId, realName, sex, birth, avatarUrl);


        LOG.info("UserInfo will be saved. {}.", userInfo);
        int insert = userInfoDao.insert(userInfo);
        LOG.info("Create userInfo :{}.", insert > 0);
        return insert > 0;
    }

    @Override
    public boolean updateUserInfoByUserId(Long userId, String realName, Integer sex, Long birth, String avatarUrl) {
        AuthUserInfo userInfo = new AuthUserInfo(userId, realName, sex, birth, avatarUrl);

        LOG.info("UserInfo will be update. {}.", userInfo);
        int update = userInfoDao.updateByUserId(userInfo);
        LOG.info("Update userInfo :{}.", update > 0);
        return update > 0;
    }


    @Override
    public Account getAccountByUniqueKey(String appId) {
        Long userId = getUserIdByAppId(appId);
        if (userId == null) {
            LOG.warn("get userId null by appId: {}.", appId);
            return null;
        }
        AuthUser user = userDao.selectByPrimaryKey(userId);
        LOG.info("Select account by uniqueKey. uk: {}.", appId);
        return user == null ? null : new Account(appId, user.getSlatPassword(), user.getSalt());
    }

    @Override
    public List<AuthUser> listAll(Integer start, Integer size) {
        List<AuthUser> users = userDao.selectAll(start, size);
        LOG.info("List user start:{}. size:{}. users:{}.", start, size, users);

        return users;
    }

    @Override
    public List<AuthUser> listByRoleId(Integer roleId, Integer start, Integer size) {
        List<AuthUser> users = userDao.selectByRoleId(roleId, start, size);
        LOG.info("List user roleId:{}. start:{}. size:{}. users:{}.", roleId, start, size, users);

        return users;
    }

    @Override
    public List<AuthUser> listByGroupId(Integer groupId, Integer start, Integer size) {
        List<AuthUser> users = userDao.selectByGroupId(groupId, start, size);
        LOG.info("List user groupId:{}. start:{}. size:{}. users:{}.", groupId, start, size, users);

        return users;
    }

    @Override
    public Long getUserIdByAppId(String appId) {
        if (appId == null) {
            LOG.warn("appId is null!!");
            return null;
        }

        Object[] appIdUserId;
        switch (CheckTools.isPhoneOrMailOrName(appId)) {
            case "mail":
                appIdUserId = getAppIdUserId(null, null, appId);
                break;
            case "phone":
                appIdUserId = getAppIdUserId(null, appId, null);
                break;
            default:
                appIdUserId = getAppIdUserId(appId, null, null);
                break;
        }
        if (appIdUserId == null) {
            LOG.warn("appIdUserId is null!! appId:{}.", appId);
            return null;
        }

        LOG.info("get userId: {} by appId:{}.", appIdUserId[1].toString(), appId);
        return Long.valueOf(appIdUserId[1].toString());
    }

    @Override
    public AuthUser getUserByPrimaryKey(Long userId) {
        AuthUser user = userDao.selectByPrimaryKey(userId);
        LOG.info("Select user by primaryKey userId:{}. user:{}.", userId, user);

        return user;
    }

    @Override
    public AuthUserInfo getUserInfoByUserId(Long userId) {
        AuthUserInfo userInfo = userInfoDao.selectByUserId(userId);
        LOG.info("Select userInfo by userId:{}. userInfo:{}.", userId, userInfo);

        return userInfo;
    }

    @Override
    public AuthUser getUserByPhone(String phone) {
        AuthUser user = userDao.selectByPhone(phone);
        LOG.info("Select user by phone:{}. user:{}.", phone, user);

        return user;
    }

    @Override
    public AuthUser getUserByUsername(String username) {
        AuthUser user = userDao.selectByUsername(username);
        LOG.info("Select user by username:{}. user:{}.", username, user);

        return user;
    }

    @Override
    public List<String> listRolesByAppId(String appId) {
        List<String> roles = userDao.selectUserRolesByUserId(getUserIdByAppId(appId));
        LOG.info("List role by appId:{}. roles:{}.", appId, roles);
        return roles;
    }

    @Override
    public String issueLoginToken(Object[] appIdUserId) {
        String appId = appIdUserId[0].toString();
        String userId = appIdUserId[1].toString();

        LOG.info("Login {}.", appId);

        //TODO 检查 登录IP 是否相同
        String dbJwt = redisTemplate.opsForValue().get("JWT-SESSION-" + userId);
        if (!StringUtils.isEmpty(dbJwt) && JsonWebTokenUtil.verifyAppIdIsJwt(appId, dbJwt)) {

            redisTemplate.opsForValue().set("JWT-SESSION-" + userId, dbJwt, tokenTimeout, TimeUnit.SECONDS);
            LOG.info("Refresh jwt done. jwt:{}.", dbJwt);
            return dbJwt;
        }

        String roles = getRolesByAppId(appId);
        String jwt = JsonWebTokenUtil.issueJwt(UUID.randomUUID().toString(), appId,
                "token-server", jwtTimeout, roles, null, SignatureAlgorithm.HS512);
        redisTemplate.opsForValue().set("JWT-SESSION-" + userId, jwt, tokenTimeout, TimeUnit.SECONDS);
        LOG.info("Generate jwt done. jwt:{}.", jwt);
        return jwt;
    }

    @Override
    public Object[] getAppIdUserId(String username, String phone, String mail) {
        String loginApp;
        Long userId;

        if (!StringUtils.isEmpty(username)) {
            loginApp = username;
            userId = userDao.selectUserIdByAppId(username, null, null);
            LOG.info("get appId:{}, userId:{} by username.", loginApp, userId);
        } else if (!StringUtils.isEmpty(phone)) {
            loginApp = phone;
            userId = userDao.selectUserIdByAppId(null, phone, null);
            LOG.info("get appId:{}, userId:{} by phone.", loginApp, userId);
        } else if (!StringUtils.isEmpty(mail)) {
            loginApp = mail;
            userId = userDao.selectUserIdByAppId(null, null, mail);
            LOG.info("get appId:{}, userId:{} by mail.", loginApp, userId);
        } else {
            throw new ServerException(ErrorEnum.INVALID_PARAMS, "account can't be null!");
        }

        if (StringUtils.isEmpty(loginApp) || userId == null) {
            LOG.warn("get appId userId failed! username:{}, phone:{}, mail:{}", username, phone, mail);
            return null;
        }
        return new Object[]{loginApp, userId};
    }

    @Override
    public boolean createUserRole(Long uid, Integer roleId) {
        int insert = userRoleDao.insert(new AuthUserRole(uid, roleId));
        LOG.info("Create user-role :{}. uid:{}. roleId:{}.", insert > 0, uid, roleId);
        return insert > 0;
    }

    @Override
    public boolean deleteUserRole(Long uid, Integer roleId) {
        int delete = userRoleDao.deleteByUniqueKey(new AuthUserRole(uid, roleId));
        LOG.info("Delete user-role :{}. uid:{}. roleId:{}.", delete > 0, uid, roleId);
        return delete > 0;
    }

    @Override
    public String getRolesByAppId(String appId) {
        List<String> roles = listRolesByAppId(appId);

        StringBuilder res = new StringBuilder();
        roles.forEach(role -> res.append(role).append(";"));
        LOG.info("Select roles string by uniqueKey. uk: {}. roles: {}.", appId, res.toString());
        return res.toString();
    }

}
