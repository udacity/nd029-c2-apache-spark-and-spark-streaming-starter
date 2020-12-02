package com.getsimplex.steptimer.model;

public class Truck {

    private String truckNumber;
    private Integer fillPercent;
    private String gearPosition;
    private Integer milesFromShop;
    private Integer odometerReading;

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public Integer getFillPercent() {
        return fillPercent;
    }

    public void setFillPercent(Integer fillPercent) {
        this.fillPercent = fillPercent;
    }

    public String getGearPosition() {
        return gearPosition;
    }

    public void setGearPosition(String gearPosition) {
        this.gearPosition = gearPosition;
    }

    public Integer getMilesFromShop() {
        return milesFromShop;
    }

    public void setMilesFromShop(Integer milesFromShop) {
        this.milesFromShop = milesFromShop;
    }

    public Integer getOdometerReading() {
        return odometerReading;
    }

    public void setOdometerReading(Integer odometerReading) {
        this.odometerReading = odometerReading;
    }
}
