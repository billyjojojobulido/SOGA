package com.example.soga.dao;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomCode;
    private int capacity;
    private List<Endpoint> endpoints;
    public Room(){
        this.endpoints = new ArrayList<>();
        this.capacity = 6;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }

    public void addEndpoint(Endpoint endpoint){
        this.endpoints.add(endpoint);
    }
}
