package com.jelly.authuser.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 桶相关的工具类
 *
 * @author guodongzhang
 */
public class CheckTools {
    private static final Logger LOG = LoggerFactory.getLogger(CheckTools.class);


    public static boolean matchRe(String re, String str) {
        Pattern p;
        Matcher m;
        boolean b = false;
        if (str != null && !"".equals(str)) {
            p = Pattern.compile(re);
            m = p.matcher(str);
            b = m.matches();
        }
        LOG.info("Regular match {}.", b);
        return b;
    }

    /**
     * 中国电信号段
     * 133,149,153,173,174,177,180,181,189,199
     * 中国联通号段
     * 130,131,132,145,146,155,156,166,175,176,185,186
     * 中国移动号段
     * 134(0-8),135,136,137,138,139,147,148,150,151,152,157,158,159,165,178,182,183,184,187,188,198
     * 上网卡专属号段（用于上网和收发短信，不能打电话）
     * 如中国联通的是145
     * 虚拟运营商
     * 电信：1700,1701,1702
     * 移动：1703,1705,1706
     * 联通：1704,1707,1708,1709,171
     * 卫星通信： 1349 <br>　　　　　未知号段：141、142、143、144、154
     */
    public static boolean checkPhone(String str) {
        LOG.info("Check Mobile {}.", str);
        String re = "^[1](([3|5|8][\\d])|([4][4,5,6,7,8,9])|([6][2,5,6,7])|([7][^9])|([9][1,8,9]))[\\d]{8}$";
        return matchRe(re, str);
    }

    /**
     * 检查邮箱
     * 名称允许汉字、字母、数字，域名只允许英文域名
     * 汉字在正则表示为[\u4e00-\u9fa5]
     * 字母和数字表示为A-Za-z0-9
     *
     * @param str 邮箱
     */
    public static boolean checkMail(String str) {
        LOG.info("Check Mail {}.", str);
        String re = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        return matchRe(re, str);
    }

    /**
     * 判断appId是手机号/邮箱/用户名
     *
     * @param str appId
     * @return String
     */
    public static String isPhoneOrMailOrName(String str) {
        if (checkPhone(str)) {
            LOG.info("str:{} is phone.", str);
            return "phone";
        } else if (checkMail(str)) {
            LOG.info("str:{} is mail.", str);
            return "mail";
        } else {
            LOG.info("str:{} is name.", str);
            return "name";
        }
    }
}
