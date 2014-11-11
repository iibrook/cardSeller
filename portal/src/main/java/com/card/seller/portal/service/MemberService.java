package com.card.seller.portal.service;

import com.card.seller.dao.MemberDao;
import com.card.seller.domain.Member;
import com.card.seller.portal.domain.MemberConstants;
import com.card.seller.portal.exception.CheckMemberException;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by minjie
 * Date:14-11-10
 * Time:下午9:59
 */
@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private GenerateService generateService;

    @Transactional
    public void checkMemberByAccount(String account) throws CheckMemberException {
        Member member = memberDao.getMemberByAccount(account);
        if (member != null) {
            throw new CheckMemberException("the user has exists", MemberConstants.USER_HAS_EXIST);
        }
    }

    public Member saveMember(String name, String pwd, String phone, String realName, String identity, String ip) {
        ByteSource salt = generateService.generateUserSalt();
        Member member = new Member();
        member.setName(name);
        member.setPhone(phone);
        member.setRealName(realName);
        member.setIdentity(identity);
        member.setRealPwd(pwd);
        member.setBalance(new BigDecimal(0));
        member.setSalt(salt.toBase64());
        member.setPwd(generateService.generatEncryptPassWord(pwd, salt));
        member.setRegisterIp(ip);
        member.setRegisterTime(new Date());
        memberDao.save(member);
        return member;
    }
}
