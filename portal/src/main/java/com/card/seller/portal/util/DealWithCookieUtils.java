package com.card.seller.portal.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lumine on 14-3-5.
 */
public class DealWithCookieUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DealWithCookieUtils.class);

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        LOGGER.debug("get cookie:{}", cookieName);
        if (request == null) {
            return null;
        }
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie != null && cookieName.equalsIgnoreCase(cookie.getName())) {
                        return URLDecoder.decode(cookie.getValue(), "utf-8");
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("get cookie happen UnsupportedEncodingException", e);
        }
        return null;

    }

    public static String getCookieValue(Cookie cookie, String cookieName) {
        LOGGER.debug("get cookie:{}", cookieName);
        try {
            if (cookie != null && cookieName.equalsIgnoreCase(cookie.getName())) {
                return URLDecoder.decode(cookie.getValue(), "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("get cookie happen UnsupportedEncodingException", e);
        }
        return null;

    }

    public static Map<String, String> initCookieMap(HttpServletRequest request, String cookiePrefix) {
        Map<String, String> map = new HashMap<String, String>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                LOGGER.debug("the cookie is {}.", cookie.getName());
                if (cookie.getName().startsWith(cookiePrefix)) {
                    try {
                        map.put(cookie.getName(), URLDecoder.decode(cookie.getValue(), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.error("get cookie happen UnsupportedEncodingException", e);
                    }
                }
            }
        }
        return map;
    }
    public static void deleteCookieMap(String cookieName, String domain, HttpServletResponse response,HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith(cookieName)) {
                        LOGGER.debug("start delete cookie{}.", cookie.getName());
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                        response.addHeader("P3P", "CP=\" OTI DSP COR IVA OUR IND COM \"");
                }
            }
        }
    }

    public static void deleteCookie(String cookieName, String domain, HttpServletResponse response) {
        LOGGER.debug("start delete cookie:{}", cookieName);
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        response.addHeader("P3P", "CP=\" OTI DSP COR IVA OUR IND COM \"");
    }

    public static void addCookie(String cookieName, String cookieValue, String domain, int maxAge, HttpServletResponse response) {
        LOGGER.debug("add cookie:{}", cookieName);
        Cookie cookie = null;
        try {
            cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue, "utf-8"));
            if (StringUtils.isNotBlank(domain)) {
                cookie.setDomain(domain);
            }
            cookie.setMaxAge(maxAge);
            cookie.setPath("/");
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("add cookie happen UnsupportedEncodingException", e);
        }
        response.addCookie(cookie);
        response.addHeader("P3P", "CP=\" OTI DSP COR IVA OUR IND COM \"");
    }

    public static void addLongCookie(String cookieName, String cookieValue, String domain, HttpServletResponse response) {
        Cookie cookie = null;
        try {
            cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue, "utf-8"));
            if (StringUtils.isNotBlank(domain)) {
                cookie.setDomain(domain);
            }
            cookie.setPath("/");
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("add long cookie happen UnsupportedEncodingException", e);
        }
        response.addCookie(cookie);
        response.addHeader("P3P", "CP=\" OTI DSP COR IVA OUR IND COM \"");
    }
}
