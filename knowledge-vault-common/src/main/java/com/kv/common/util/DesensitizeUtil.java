package com.kv.common.util;

/**
 * 数据脱敏工具
 */
public class DesensitizeUtil {

    /** 手机号脱敏：138****5678 */
    public static String phone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /** 邮箱脱敏：t***@example.com */
    public static String email(String email) {
        if (email == null || !email.contains("@")) return email;
        int at = email.indexOf("@");
        return email.charAt(0) + "***" + email.substring(at);
    }

    /** 身份证脱敏：350***********1234 */
    public static String idCard(String idCard) {
        if (idCard == null || idCard.length() < 8) return idCard;
        return idCard.substring(0, 3) + "***********" + idCard.substring(idCard.length() - 4);
    }

    /** 姓名脱敏：张* */
    public static String name(String name) {
        if (name == null || name.isEmpty()) return name;
        if (name.length() == 1) return "*";
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }
}
