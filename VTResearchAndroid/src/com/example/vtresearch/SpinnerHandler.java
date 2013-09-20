package com.example.vtresearch;

/**
 * Changes the spinner values so BT4U can work.
 * 
 */
public class SpinnerHandler {

	public String convertToShortName(String value) {
		if (value.equals("Harding")) {
			return "HDG";
		}
		if (value.equals("Hethwood A") || value.equals("Hethwood B")
				|| value.equals("Hethwood / Harding")) {
			return "HWD";
		}
		if (value.equals("Hokie Express")) {
			return "HXP";
		}
		if (value.equals("Main Street South")) {
			return "MSS";
		}
		if (value.equals("Progress")) {
			return "PRG";
		}
		if (value.equals("Tom's Creek")) {
			return "TC";
		}
		if (value.equals("Two Town Trolley")) {
			return "TTT";
		}
		if (value.equals("University City Boulevard")) {
			return "UCB";
		}
		if (value.equals("University Mall Shuttle")) {
			return "UMS";
		}

		return "";

	}
}
