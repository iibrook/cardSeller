package com.card.seller.portal.controller;

import com.card.seller.domain.AnalyzeIpUtils;
import com.card.seller.domain.CaptchaUtils;
import com.card.seller.domain.Member;
import com.card.seller.portal.domain.MemberConstants;
import com.card.seller.portal.exception.CheckMemberException;
import com.card.seller.portal.service.MemberService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by minjie
 * Date:14-11-10
 * Time:下午9:33
 */
@Controller
public class RegisterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/toRegister", method = RequestMethod.GET)
    public String toRegister(@RequestParam(value = "redirectUrl", required = false) String redirectUrl, Map<String, Object> viewObject) {
        return "/toRegister";
    }

    @RequestMapping("/getCaptcha")
    public ResponseEntity<byte[]> getCaptcha(HttpSession session) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String captcha = CaptchaUtils.getCaptcha(80, 32, 5, outputStream).toLowerCase();

        session.setAttribute("captcha", captcha);
        byte[] bs = outputStream.toByteArray();
        outputStream.close();
        return new ResponseEntity<byte[]>(bs, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkRegName", method = RequestMethod.POST)
    @ResponseBody
    public int checkRegName(@Valid @RequestParam("regName") String regName) {
        int resultCode = MemberConstants.SUCCESS;
        try {
            memberService.checkMemberByAccount(regName);
        } catch (CheckMemberException e) {
            LOGGER.error("check regName error" + e.getMessage());
            LOGGER.error("check regName error :" + e.getMessage());
            resultCode = e.getResultCode();
        } catch (Exception e) {
            LOGGER.error("check regName error", e);
            resultCode = MemberConstants.OTHER_ERROR;
        }
        return resultCode;
    }

    @RequestMapping(value = "/checkCaptcha", method = RequestMethod.POST)
    @ResponseBody
    public int checkCaptcha(@Valid @RequestParam("imageCode") String imageCode, HttpSession session) {
        String captcha = (String) session.getAttribute("captcha");
        LOGGER.info("captcha is {} and the imageCode is {}", captcha, imageCode);
        if (imageCode.equalsIgnoreCase(captcha)) {
            return MemberConstants.SUCCESS;
        }
        return MemberConstants.CAPTCHA_ERROR;
    }

    @RequestMapping(value = "regist", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> regist(HttpServletRequest request, @RequestParam(value = "name", required = true) String name, @RequestParam(value = "pwd", required = true) String pwd, @RequestParam(value = "phone", required = true) String phone, @RequestParam(value = "realName", required = true) String realName, @RequestParam(value = "identity", required = true) String identity) {
        Map<String, Object> jsonMap = Maps.newHashMap();
        String ip = AnalyzeIpUtils.getIpAddr(request);
        Member member = memberService.saveMember(name, pwd, phone, realName, identity, ip);
        jsonMap.put("name", member.getName());
        jsonMap.put("phone", member.getPhone());
        return jsonMap;
    }
}
