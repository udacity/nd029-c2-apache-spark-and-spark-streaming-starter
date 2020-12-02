package com.getsimplex.steptimer.model;

public class StartSimulation {

    private int numberOfCustomers;

    public StartSimulation(int numberOfCustomers){
        this.numberOfCustomers=numberOfCustomers;
    }

    public int getNumberOfCustomers() {
        return numberOfCustomers;
    }

    public void setNumberOfCustomers(int numberOfCustomers) {
        this.numberOfCustomers = numberOfCustomers;
    }
}
