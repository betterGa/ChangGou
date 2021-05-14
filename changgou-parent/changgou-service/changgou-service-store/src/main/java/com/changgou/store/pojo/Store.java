package com.changgou.store.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "tb_store")
public class Store implements Serializable {

    @Id
    @Column(name = "storename")
    private String storeName;

    @Column(name = "password")
    private String password;

    @Column(name = "invitecode")
    private String inviteCode;

    @Column(name = "centerstatus")
    private String centerStatus;


    @Column(name = "status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // @Column(name = "created")
    // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone ="GTM+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date created;

    // @Column(name = "updated")
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone ="GTM+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updated;

    @Column(name = "score")
    private Integer score;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

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
}


