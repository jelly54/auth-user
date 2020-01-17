package com.jelly.authuser.util;

import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletResponse;
import java.io.PrintWriter;
import java.util.Random;

/**
 * 高频方法工具类
 *
 * @author guodongzhang
 */
public class CommonUtil {

    /**
     * 获取指定位数的随机数
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void responseWrite(String outStr, ServletResponse response) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        PrintWriter printWriter = null;
        try {
            printWriter = WebUtils.toHttp(response).getWriter();
            printWriter.write(outStr);
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (null != printWriter) {
                printWriter.close();
            }
        }
    }
}
