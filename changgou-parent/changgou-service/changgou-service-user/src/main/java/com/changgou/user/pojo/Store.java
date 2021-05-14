package com.changgou.user.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "tb_store")
public class Store implements Serializable {
    // 商家名称
    @Id
    @Column(name = "storename")
    private String storeName;

    // 商家密码
    @Column(name = "password")
    private String password;

    // 平台邀请码
    @Column(name = "invitecode")
    private String inviteCode;

    // 入驻状态。1表示允许入驻，0表示未入驻
    @Column(name = "centerstatus")
    private String centerStatus;

    // 创建时间
    @Column(name = "created")
    private Date created;

    // 修改时间
    @Column(name = "updated")
    private Date datetime;

    // 商家评分
    @Column(name = "score")
    private Integer score;

    // 使用状态。1表示正常使用，0表示状态异常
    @Column(name = "status")
    private String status;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getCenterStatus() {
        return centerStatus;
    }

    public void setCenterStatus(String centerStatus) {
        this.centerStatus = centerStatus;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}






