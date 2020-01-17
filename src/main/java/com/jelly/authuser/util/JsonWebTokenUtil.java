package com.jelly.authuser.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jelly.authuser.entity.bo.JwtAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.compression.DefaultCompressionCodecResolver;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author guodongzhang
 */
public class JsonWebTokenUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JsonWebTokenUtil.class);

    public static final String SECRET_KEY = "?::4343fdf4fdf6cvf):";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static CompressionCodecResolver codecResolver = new DefaultCompressionCodecResolver();

    private JsonWebTokenUtil() {

    }

    /**
     * json web token 签发
     *
     * @param id          令牌ID
     * @param subject     用户ID
     * @param issuer      签发人
     * @param period      有效时间(毫秒)
     * @param roles       访问主张-角色
     * @param permissions 访问主张-权限
     * @param algorithm   加密算法
     * @return java.lang.String
     */
    public static String issueJwt(String id, String subject, String issuer, Long period, String roles, String permissions, SignatureAlgorithm algorithm) {
        LOG.info("Issue jwt. [id:{}, subject:{}, issuer:{}, period:{},roles:{},permissions:{},algorithm:{}]",
                id, subject, issuer, period, roles, permissions, algorithm);

        long currentTimeMillis = System.currentTimeMillis();
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        JwtBuilder jwtBuilder = Jwts.builder();
        if (!StringUtils.isEmpty(id)) {
            jwtBuilder.setId(id);
        }
        if (!StringUtils.isEmpty(subject)) {
            jwtBuilder.setSubject(subject);
        }
        if (!StringUtils.isEmpty(issuer)) {
            jwtBuilder.setIssuer(issuer);
        }
        jwtBuilder.setIssuedAt(new Date(currentTimeMillis));
        if (null != period) {
            jwtBuilder.setExpiration(new Date(currentTimeMillis + period * 1000));
        }
        if (!StringUtils.isEmpty(roles)) {
            jwtBuilder.claim("roles", roles);
        }
        if (!StringUtils.isEmpty(permissions)) {
            jwtBuilder.claim("perms", permissions);
        }
        jwtBuilder.compressWith(CompressionCodecs.DEFLATE);
        jwtBuilder.signWith(algorithm, secretKeyBytes);

        LOG.info("Issue jwt done.");
        return jwtBuilder.compact();
    }

    /**
     * 解析JWT的Payload
     */
    public static String parseJwtPayload(String jwt) {
        LOG.debug("Parse jwt. jwt:{}", jwt);

        Assert.hasText(jwt, "JWT String argument cannot be null or empty.");
        String base64UrlEncodedHeader = null;
        String base64UrlEncodedPayload = null;
        String base64UrlEncodedDigest = null;
        int delimiterCount = 0;
        StringBuilder sb = new StringBuilder(128);
        for (char c : jwt.toCharArray()) {
            if (c == '.') {
                CharSequence tokenSeq = io.jsonwebtoken.lang.Strings.clean(sb);
                String token = tokenSeq != null ? tokenSeq.toString() : null;

                if (delimiterCount == 0) {
                    base64UrlEncodedHeader = token;
                } else if (delimiterCount == 1) {
                    base64UrlEncodedPayload = token;
                }

                delimiterCount++;
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (delimiterCount != 2) {
            String msg = "JWT strings must contain exactly 2 period characters. Found: " + delimiterCount;
            throw new MalformedJwtException(msg);
        }
        if (sb.length() > 0) {
            base64UrlEncodedDigest = sb.toString();
        }
        if (base64UrlEncodedPayload == null) {
            throw new MalformedJwtException("JWT string '" + jwt + "' is missing a body/payload.");
        }

        // =============== Header =================
        Header header;
        CompressionCodec compressionCodec = null;
        if (base64UrlEncodedHeader != null) {
            String origValue = TextCodec.BASE64URL.decodeToString(base64UrlEncodedHeader);
            Map<String, Object> m = readValue(origValue);
            if (base64UrlEncodedDigest != null) {
                header = new DefaultJwsHeader(m);
            } else {
                header = new DefaultHeader(m);
            }
            compressionCodec = codecResolver.resolveCompressionCodec(header);
        }

        // =============== Body =================
        String payload;
        if (compressionCodec != null) {
            byte[] decompressed = compressionCodec.decompress(TextCodec.BASE64URL.decode(base64UrlEncodedPayload));
            payload = new String(decompressed, io.jsonwebtoken.lang.Strings.UTF_8);
        } else {
            payload = TextCodec.BASE64URL.decodeToString(base64UrlEncodedPayload);
        }

        LOG.debug("Parse jwt done. payload:{}", payload);
        return payload;
    }

    /**
     * 验签JWT
     *
     * @param jwt json web token
     */
    public static JwtAccount parseJwt(String jwt, String appKey) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        LOG.debug("Parse jwt to JwtAccount. jwt:{}. appKey:{}.", jwt, appKey);

        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(appKey))
                .parseClaimsJws(jwt)
                .getBody();
        JwtAccount jwtAccount = new JwtAccount();
        // 令牌ID
        jwtAccount.setTokenId(claims.getId());
        // 客户标识
        jwtAccount.setAppId(claims.getSubject());
        // 签发者
        jwtAccount.setIssuer(claims.getIssuer());
        // 签发时间
        jwtAccount.setIssuedAt(claims.getIssuedAt());
        // 接收方
        jwtAccount.setAudience(claims.getAudience());
        // 访问主张-角色
        jwtAccount.setRoles(claims.get("roles", String.class));
        // 访问主张-权限
        jwtAccount.setPerms(claims.get("perms", String.class));

        LOG.debug("Parse jwt to JwtAccount done. jwtAccount:{}.", jwtAccount.toString());
        return jwtAccount;
    }


    /**
     * 验证jwt是否属于appId
     *
     * @param appId appId
     * @param jwt   jwtToken
     */
    public static boolean verifyAppIdIsJwt(String appId, String jwt) {
        JwtAccount jwtAccount;
        try {
            jwtAccount = parseJwt(jwt, SECRET_KEY);
        } catch (Exception e) {
            LOG.warn("parseJwt has exception!! appId:{}, jwt:{}.", appId, jwt);
            e.printStackTrace();
            return false;
        }
        if (StringUtils.isEmpty(appId) || !appId.equalsIgnoreCase(jwtAccount.getAppId())) {
            LOG.warn("app_id:{} does not match authorization:{}.", appId, jwt);
            return false;
        }
        return true;
    }

    /**
     * 从json数据中读取格式化map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readValue(String val) {
        try {
            return MAPPER.readValue(val, Map.class);
        } catch (IOException e) {
            throw new MalformedJwtException("Unable to read JSON value: " + val, e);
        }
    }

    /**
     * 分割字符串进SET
     */
    @SuppressWarnings("unchecked")
    public static Set<String> split(String str) {
        Set<String> set = new HashSet<>();
        if (StringUtils.isEmpty(str)) {
            return set;
        }
        set.addAll(CollectionUtils.arrayToList(str.split(";")));
        return set;
    }

}
