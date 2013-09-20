package com.example.vtresearch;

import java.util.ArrayList;

import android.content.Context;

public class MasterMap {
	ArrayList<BusRoute> busRoutes;
	Parser parse;
	Grouping hm;
	Context context;

	public MasterMap(Context context) {
		busRoutes = new ArrayList<BusRoute>();
		parse = new Parser(context);
	}

	public void fillMap(String fileName) {

		ArrayList<Stop> stops = parse.parseStops(fileName);
		hm = new Grouping(stops);

		for (int i = 0; i < stops.size(); i++) {
			hm.addToList(new Location(stops.get(i).lattitude,
					stops.get(i).longitude));
		}

		hm.addToMap();
	}

	public void fillBusArray(String fileName, int busIndex) {
		busRoutes.add(busIndex, new BusRoute());
		busRoutes.get(busIndex).fillList(fileName, parse);
	}
}
