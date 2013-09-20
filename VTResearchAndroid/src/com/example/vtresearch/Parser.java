package com.example.vtresearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

public class Parser extends Activity {

	Context context;

	public Parser(Context context) {
		this.context = context;
	}

	public String[] splitLine(String line) {
		String[] lineInfo = line.split("\t");
		return lineInfo;
	}

	public ArrayList<Stop> parseStops(String fileName) {
		ArrayList<Stop> stops = new ArrayList<Stop>();
		AssetManager assetManager = context.getAssets();
		InputStreamReader input;
		try {
			input = new InputStreamReader(assetManager.open(fileName));
			BufferedReader reader = new BufferedReader(input);
			String line;
			while ((line = reader.readLine()) != null) {
				String[] lineInfo = splitLine(line);
				StringBuilder longCat = new StringBuilder(lineInfo[3]);
				StringBuilder latCat = new StringBuilder(lineInfo[2]);
				if (lineInfo[2].length() < 8) {
					int numZeros = 8 - lineInfo[2].length();
					for (int i = 0; i < numZeros; i++) {
						latCat.append("0");
					}
				}

				if (lineInfo[3].length() < 9) {
					int numZeros = 9 - lineInfo[2].length();
					for (int i = 0; i < numZeros; i++) {
						longCat.append("0");
					}
				}

				Stop stop = new Stop(lineInfo[1], lineInfo[0],
						latCat.toString(), longCat.toString(), false);
				stops.add(stop);
			}
			input.close();
		} catch (IOException e) {
			System.out.println(fileName + " is not found!");
			e.printStackTrace();
		}

		return stops;
	}
}
