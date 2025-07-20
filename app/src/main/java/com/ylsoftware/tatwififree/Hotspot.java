package com.ylsoftware.tatwififree;

import java.io.Serializable;

public class Hotspot implements Serializable {
    public String address;
    public double lat;
    public double lon;
    public float distance;

    public Hotspot() {
        this.distance = -1;
    }

    public Hotspot(String address, double lat, double lon)   {
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.distance = -1;
    }
}
