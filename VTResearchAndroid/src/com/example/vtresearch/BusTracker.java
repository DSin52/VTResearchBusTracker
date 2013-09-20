package com.example.vtresearch;

import android.app.Activity;
import android.content.Context;

public class BusTracker extends Activity {

	MasterMap mapper;
	Context context;

	public BusTracker(Context context) {
		this.context = context;
	}

	public void fillMasterInformation() {

		mapper = new MasterMap(context);

		mapper.fillMap("stopscsv.txt");
		mapper.fillBusArray("hdg.txt", 0);
		mapper.fillBusArray("hwda.txt", 1);
		mapper.fillBusArray("hwdb.txt", 2);
		mapper.fillBusArray("hwdhdg.txt", 3);
		mapper.fillBusArray("hxp.txt", 4);
		mapper.fillBusArray("mss.txt", 5);
		mapper.fillBusArray("prg.txt", 6);
		mapper.fillBusArray("tc.txt", 7);
		mapper.fillBusArray("ttt.txt", 8);
		mapper.fillBusArray("ucb.txt", 9);
		mapper.fillBusArray("ums.txt", 10);
	}

}
