package com.property.util;

import org.mindrot.jbcrypt.BCrypt;

public class BcryptUtil {

    /**
     * 加密密码
     */
    public static String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 校验密码
     */
    public static boolean check(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }
}
