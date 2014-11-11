package com.card.seller.portal.util;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * User: minj
 * Date: 13-12-6
 * Time: 下午3:23
 */
public class SecurityContext {

    public static final String PRINCIPAL = "principal";

    public static boolean isAuthenticate() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isAuthenticated();
    }

    public static String getAccount() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isAuthenticated() ? subject.getPrincipal().toString() : null;
    }

    public static Object getPrincipal() {
        Subject subject = SecurityUtils.getSubject();
        return subject != null && subject.isAuthenticated() ? subject.getPrincipal() : null;
    }
}
