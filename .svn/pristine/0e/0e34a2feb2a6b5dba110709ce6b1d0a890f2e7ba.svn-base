package com.card.seller.dao;

import com.card.seller.dao.hibernate.HibernateSupportDao;
import com.card.seller.domain.Member;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by minjie
 * Date:14-11-10
 * Time:下午9:30
 */
public class MemberDao extends HibernateSupportDao<Member, Long> {

    public Member getMemberByAccount(String account) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("account", account);
        return findUniqueByQuery("from " + Member.class.getName() + " where name=:account", map);
    }
}
