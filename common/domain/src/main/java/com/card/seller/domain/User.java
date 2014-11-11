package com.card.seller.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by minjie
 * Date:14-11-10
 * Time:下午8:53
 */
@Entity
@Table(name = "users")
@SequenceGenerator(name = "seq_gen", sequenceName = "seq_users", allocationSize = 1)
public class User extends IdEntity {

    //登录名
    private String name;
    //密码
    private String pwd;
    //最后登录时间
    private Date lastLoginTime;
    //salt
    private String salt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Column(name = "last_login_time")
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
