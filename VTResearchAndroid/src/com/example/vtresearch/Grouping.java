package com.example.vtresearch;

import java.util.ArrayList;
import java.util.HashMap;

public class Grouping {

	HashMap<Location, Stop> group;
	ArrayList<Location> locations;
	ArrayList<Stop> stopList;

	public Grouping(ArrayList<Stop> stops) {
		group = new HashMap<Location, Stop>();
		locations = new ArrayList<Location>();
		stopList = stops;
	}

	public boolean addPair(Location loc, Stop stop) {
		if (!group.containsKey(loc)) {
			group.put(loc, stop);
			return true;
		}
		return false;
	}

	public Stop getStopCode(Location loc) {
		if (group.containsKey(locations.get(getCorrectLoc(loc))) == true) {
			return group.get(locations.get(getCorrectLoc(loc)));
		}
		return null;

	}

	public int getCorrectLoc(Location loc) {
		int i;
		for (i = 0; i < locations.size(); i++) {
			String hmLat = locations.get(i).lattitude.substring(0, 7);
			String hmLon = locations.get(i).longitude.substring(0, 8);
			String userLat = loc.lattitude.substring(0, 7);
			String userLon = loc.longitude.substring(0, 8);

			Double hmLat1 = Double.parseDouble(hmLat);
			Double userLat1 = Double.parseDouble(userLat);
			Double hmLon1 = Double.parseDouble(hmLon);
			Double userLon1 = Double.parseDouble(userLon);

			Boolean latbool = Math.abs(hmLat1 - userLat1) <= 0.0005;
			Boolean lonbool = Math.abs(hmLon1 - userLon1) <= 0.0005;
			Boolean result = latbool && lonbool;

			if (result) {
				return i;
			}
		}
		return -1;
	}

	public void addToList(Location loc) {
		locations.add(loc);
	}

	public void addToMap() {
		for (int i = 0; i < stopList.size(); i++) {
			addPair(locations.get(i), stopList.get(i));
		}
	}
}
