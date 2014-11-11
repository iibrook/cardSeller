package com.card.seller.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by minjie
 * Date:14-11-10
 * Time:下午9:17
 */
@Entity
@Table(name = "member")
@SequenceGenerator(name = "seq_gen", sequenceName = "seq_users", allocationSize = 1)
public class Member extends IdEntity {

    private String name;

    private String pwd;

    private String realPwd;

    private String phone;

    private String realName;

    private String identity;

    private BigDecimal balance;

    private String salt;

    private String registerIp;

    private Date registerTime;

    private String lastLoginIp;

    private Date lastLoginTime;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "real_name")
    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Column(name = "balance", nullable = false, precision = 12, scale = 3)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Column(name = "real_pwd")
    public String getRealPwd() {
        return realPwd;
    }

    public void setRealPwd(String realPwd) {
        this.realPwd = realPwd;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Column(name = "register_ip", length = 20)
    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "register_time", length = 30)
    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    @Column(name = "last_login_ip", length = 20)
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_time", length = 30)
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
