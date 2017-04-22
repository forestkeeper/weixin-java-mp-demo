package com.github.binarywang.demo.spring.domain;

import java.util.Date;

/**
 * Created by wen on 2017/4/22.
 *
 * 一次预约的具体记录
 *
 */
public class Appointment {
    private String name;
    private String chepai;
    private Date date;
    private String driverLicense;
    private String tel;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChepai() {
        return chepai;
    }

    public void setChepai(String chepai) {
        this.chepai = chepai;
    }
}
