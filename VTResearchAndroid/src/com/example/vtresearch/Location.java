package com.example.vtresearch;

public class Location {

	public String lattitude;
	public String longitude;

	public Location(String lat, String lon) {
		this.lattitude = lat;
		this.longitude = lon;
	}

	public String toString() {
		return lattitude + ", " + longitude;
	}

}
