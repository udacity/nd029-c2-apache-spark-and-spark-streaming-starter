package com.getsimplex.steptimer.model;

import java.util.Date;

public class CustomerRisk {

    private String customer;
    private Float score;
    private Date riskDate;
    private Date riskDateLong;
    private Integer birthYear;


    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public Date getRiskDate() {
        return riskDate;
    }

    public void setRiskDate(Date riskDate) {
        this.riskDate = riskDate;
    }

    public Date getRiskDateLong() {
        return riskDateLong;
    }

    public void setRiskDateLong(Date riskDateLong) {
        this.riskDateLong = riskDateLong;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }
}
