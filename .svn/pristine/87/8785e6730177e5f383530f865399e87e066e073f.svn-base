package com.card.seller.portal.controller;

import com.card.seller.portal.util.DealWithCookieUtils;
import com.card.seller.portal.util.SecurityContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by minjie
 * Date:14-11-11
 * Time:下午2:17
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login")
    public String login(HttpServletResponse response, HttpServletRequest request, Model model) {
        DealWithCookieUtils.deleteCookie("redirectUrl", null, response);
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirectUrl = request.getParameter("redirectUrl");
        if (SecurityContext.isAuthenticate()) {
            if (StringUtils.isNotBlank(redirectUrl)) {
                return "redirect:" + redirectUrl;
            } else {
                return "redirect:/";
            }
        } else {
            if (StringUtils.isNotBlank(redirectUrl)) {
                DealWithCookieUtils.addCookie("redirectUrl", redirectUrl, null, 15 * 60 * 1000, response);
            }
        }
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        if (StringUtils.isNotEmpty(redirectUrl)) {
            model.addAttribute("redirectUrl", redirectUrl);
        }
        return "/login";
    }
}
