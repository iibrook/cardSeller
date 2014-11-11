package com.card.seller.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by lumine on 13-12-27.
 */
public class AnalyzeIpUtils {
    private static final String CDN_SRC_IP = "Cdn-Src-Ip";
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalyzeIpUtils.class);

    private static boolean isIpNull(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }


    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(CDN_SRC_IP);
        LOGGER.debug("get the Cdn-Src-Ip ip is:" + ip);
        if (isIpNull(ip)) {
            ip = request.getHeader(X_FORWARDED_FOR);
            LOGGER.debug("get the X-FORWARDED-FOR ip is:" + ip);
        }
        if (isIpNull(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
            LOGGER.debug("get the Proxy-Client-IP ip is:" + ip);
        }
        if (isIpNull(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
            LOGGER.debug("get the WL-Proxy-Client-IP ip is:" + ip);
        }
        if (isIpNull(ip)) {
            ip = request.getRemoteAddr();
            LOGGER.debug("get the remote ip is:" + ip);
        }
        return ip.trim();
    }
}
