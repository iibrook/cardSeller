package com.card.seller.portal.service;

import com.card.seller.dao.MemberDao;
import com.card.seller.portal.domain.MemberConstants;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by lumine on 13-12-25.
 */
@Service
public class GenerateService {
    @Autowired
    private MemberDao memberDao;

    public ByteSource generateUserSalt() {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        ByteSource salt = rng.nextBytes();
        return salt;
    }


    public String generatEncryptPassWord(String password, ByteSource salt) {
        return new Sha256Hash(password, salt, MemberConstants.HASH_INTERATIONS).toBase64();
    }

    public String generatePassword(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

}
