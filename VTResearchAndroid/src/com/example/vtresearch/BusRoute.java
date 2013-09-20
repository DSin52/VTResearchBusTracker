package com.example.vtresearch;

import java.util.ArrayList;

public class BusRoute {

	ArrayList<Stop> stops;

	public BusRoute() {
		stops = new ArrayList<Stop>();
	}

	public void fillList(String fileName, Parser parser) {
		this.stops = parser.parseStops(fileName);

	}

	public int findStopUsingStopCode(String code) {
		for (int i = 0; i < stops.size(); i++) {
			if (stops.get(i).stopCode.equals(code)) {
				return i;
			}
		}
		return -1;
	}

	public String getNearestBus(int userIndex) {
		return stops.get(userIndex).stopCode;
	}

}
