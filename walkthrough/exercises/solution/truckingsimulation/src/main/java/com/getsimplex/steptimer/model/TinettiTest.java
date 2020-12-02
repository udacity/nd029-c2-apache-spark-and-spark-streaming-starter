package com.getsimplex.steptimer.model;

import java.util.Date;

/**
 * Created by Administrator on 1/9/2017.
 */
public class TinettiTest {

    String userName;
    Date date;
    String raterName;
    String location;
    Integer overAllGait;
    Integer overAllBalance;
    Integer overAllScore; // overAllGait + overAllBalance

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRaterName() {
        return raterName;
    }

    public void setRaterName(String raterName) {
        this.raterName = raterName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getOverAllGait() {
        return overAllGait;
    }

    public void setOverAllGait(Integer overAllGait) {
        this.overAllGait = overAllGait;
    }

    public Integer getOverAllBalance() {
        return overAllBalance;
    }

    public void setOverAllBalance(Integer overAllBalance) {
        this.overAllBalance = overAllBalance;
    }

    public Integer getOverAllScore() {
        return overAllScore;
    }

    public void setOverAllScore(Integer overAllScore) {
        this.overAllScore = overAllScore;
    }
}
