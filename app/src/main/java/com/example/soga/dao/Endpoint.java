package com.example.soga.dao;

public class Endpoint {
    private double lat;
    private double lng;
    private String address;
    private String taskId;
    public Endpoint(){

    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
