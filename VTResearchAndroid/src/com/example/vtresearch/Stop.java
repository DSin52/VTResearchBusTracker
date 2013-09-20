package com.example.vtresearch;

public class Stop {

	String lattitude;
	String longitude;
	String stopName;
	String stopCode;
	boolean isBusHere;

	public Stop(String stopName, String stopCode, String lattitude,
			String longitude, boolean isBusHere) {
		this.lattitude = lattitude;
		this.longitude = longitude;
		this.stopName = stopName;
		this.stopCode = stopCode;
		this.isBusHere = isBusHere;
	}
	
	public String toString()
	{
		return "Stop name is: " + stopName + " Stop code is: " + stopCode + " Location: " + lattitude + ", " + longitude;
	}

}
