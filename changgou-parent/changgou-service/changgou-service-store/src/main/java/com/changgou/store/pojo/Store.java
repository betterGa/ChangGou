package com.changgou.store.pojo;

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

    @Column(name = "created")
    private Date created;

    @Column(name = "updated")
    private Date updated;

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


