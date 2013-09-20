package com.example.vtresearch;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import oauth.signpost.OAuth;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.twitter.TwitterUtils;

public class MainActivity extends Activity {
	Button sendTweet, getTweets;
	String busName, userBusStop;
	EditText busStop;
	android.location.Location userLocation;
	BusTracker bT;
	Spinner spin;
	int busRoutePosition;
	private SharedPreferences prefs;
	private LocationManager locationManager;
	private LocationListener locationListener;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		bT = new BusTracker(getApplicationContext());
		bT.fillMasterInformation();
		Editor edit = prefs.edit();
		edit.putString(OAuth.OAUTH_TOKEN,
				"1300274934-zShlGs49Vnsb3kII2AIYixO51Uz3bLK9BBkDAfq");
		edit.putString(OAuth.OAUTH_TOKEN_SECRET,
				"Yi1gdfdvoOVcEv3MRDP4pWTizCgEZtSWC7QZ1AutWY");
		edit.commit();
		getTweets = (Button) findViewById(R.id.button1);
		busStop = (EditText) findViewById(R.id.editText2);
		sendTweet = (Button) findViewById(R.id.button2);
		spin = (Spinner) findViewById(R.id.spinner1);

		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.busRoutes,
						android.R.layout.simple_spinner_item);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spin.setAdapter(adapter);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		String locationProvider = LocationManager.GPS_PROVIDER;
		locationManager.requestLocationUpdates(locationProvider, 5000, 10,
				locationListener);
		Toast locationToast = Toast.makeText(getApplicationContext(),
				"Location has been captured", Toast.LENGTH_SHORT);
		locationToast.show();

		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				String busName = adapter.getItem(pos).toString();
				setBusName(busName.replace(" ", "_"));
				setBusRoutePosition(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		sendTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Date currentTime = Calendar.getInstance().getTime();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"hh:mm:ssa EEE, d MMM yyyy");

				String time = formatter.format(currentTime);
				String lat = String.format("%.5f", getLocation().getLatitude());
				String lon = String
						.format("%.5f", getLocation().getLongitude());

				Location userLocation = new Location(lat, lon);
				

				SendTweet sender = new SendTweet();
				try {

					userBusStop = bT.mapper.hm.getStopCode(userLocation).stopCode;
					if (bT.mapper.busRoutes.get(getBusRoutePosition())
							.findStopUsingStopCode(userBusStop) < 0) {
						genericDialogBox("That Bus Stop Does not Exist.",
								v.getContext());
					} else {
						sender.execute(getBusName(), userBusStop, time).get();
					}

				} catch (Exception e) {
					genericDialogBox(
							"You are not at a valid bus stop location.",
							v.getContext());
				}
				sender.cancel(true);

			}

		});

		getTweets.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (busStop.getText() == null
						|| busStop.getText().toString().equals("")) {
					try {

						String lat = String.format("%.5f", getLocation()
								.getLatitude());
						String lon = String.format("%.5f", getLocation()
								.getLongitude());
						Location userLocation = new Location(lat, lon);
						Stop nullCheckStop = bT.mapper.hm
								.getStopCode(userLocation);
						if (bT.mapper.busRoutes.get(getBusRoutePosition())
								.findStopUsingStopCode(nullCheckStop.stopCode) < 0) {
							genericDialogBox("That Bus Stop Does not Exist.",
									v.getContext());

						} else {
							userBusStop = bT.mapper.hm
									.getStopCode(userLocation).stopCode;

							GetTweet tweeter = new GetTweet();
							String[] parsedTweet = tweeter.execute(userBusStop)
									.get();
							tweeter.cancel(true);

							genericDialogBox(
									prepareDate(parsedTweet, userBusStop),
									v.getContext());

						}
					} catch (NullPointerException n) {
						try {

							genericDialogBox(
									prepareDate(new String[3], userBusStop),
									v.getContext());
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (Exception e) {

						genericDialogBox(
								"You are not at a valid bus stop location.",
								v.getContext());
					}
				} else {
					userBusStop = busStop.getText().toString();
					try {

						if (bT.mapper.busRoutes.get(getBusRoutePosition())
								.findStopUsingStopCode(userBusStop) < 0) {
							genericDialogBox("That Bus Stop Does not Exist.",
									v.getContext());
						} else {
							GetTweet tweeter = new GetTweet();
							String[] parsedTweet = tweeter.execute(userBusStop)
									.get();
							if (parsedTweet != null) {

								genericDialogBox(
										prepareDate(parsedTweet, userBusStop),
										v.getContext());
								tweeter.cancel(true);
							} else {
								genericDialogBox(
										prepareDate(new String[3], userBusStop),
										v.getContext());
							}
							// genericDialogBox("test", v.getContext());

						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (NullPointerException e) {

					}

				}
			}

		});

	}

	public void genericDialogBox(String message, Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setTitle("Bus Status");

		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

	public String prepareDate(String[] parsedTweet, String currentStop)
			throws ParseException, InterruptedException, ExecutionException {
		SimpleDateFormat tweetFormatter = new SimpleDateFormat(
				"hh:mm:ssa EEE, d MMM yyyy");
		BackUp backUp = new BackUp();
		try {
			if (parsedTweet.length > 3) {
				Date tweetDate = tweetFormatter.parse(parsedTweet[5] + " "
						+ parsedTweet[6] + " " + parsedTweet[7] + " "
						+ parsedTweet[8] + " " + parsedTweet[9]);
				System.out.println(tweetDate.toString());

				/**
				 * TODO More thoughts... -What if BT4U is down and no info on
				 * twitter.
				 */

				String temp = getBusName();
				temp = temp.replace("_", " ");
				SpinnerHandler spinHandler = new SpinnerHandler();
				String btString = "";
				String stopShortName = spinHandler.convertToShortName(temp);
				String stopCode = currentStop;
				btString = backUp.execute(stopShortName, stopCode).get();

				backUp.cancel(true);
				SimpleDateFormat btFormatter = new SimpleDateFormat(
						"M/d/yyyy hh:mm:ss a");
				Date btDate;
				btDate = btFormatter.parse(btString.toString());
				Calendar calTweet = Calendar.getInstance();
				calTweet.setTime(tweetDate);

				if (Math.abs(calTweet.getTimeInMillis()
						- Calendar.getInstance().getTimeInMillis()) > 600000) {

					return "Best estimate from BT:\nBus will be at " + stopCode
							+ " on " + btDate.toString();
				}
				return "Best estimate from Twitter:\nBus was at "
						+ parsedTweet[3] + " on " + tweetDate.toString();
			} else {

				String temp = getBusName();
				temp = temp.replace("_", " ");
				SpinnerHandler spinHandler = new SpinnerHandler();
				String btString = "";
				String stopShortName = spinHandler.convertToShortName(temp);
				String stopCode = currentStop;
				btString = backUp.execute(stopShortName, stopCode).get();

				backUp.cancel(true);
				SimpleDateFormat btFormatter = new SimpleDateFormat(
						"M/d/yyyy hh:mm:ss a");
				Date btDate;
				btDate = btFormatter.parse(btString.toString());
				return "Best estimate from BT:\nBus will be at " + stopCode
						+ " on " + btDate.toString();
			}

		} catch (Exception e) {
			backUp.cancel(true);
			Date tweetDate = tweetFormatter.parse(parsedTweet[5] + " "
					+ parsedTweet[6] + " " + parsedTweet[7] + " "
					+ parsedTweet[8] + " " + parsedTweet[9]);
			return "BT4U is currently down.\nTwitter Feed: " + "Bus was at "
					+ parsedTweet[3] + " on " + tweetDate.toString();
		}

	}

	// public void displayDialogBox(String parsedTweet, View v) {
	// AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
	// v.getContext());
	//
	// alertDialogBuilder.setTitle("Bus Status");
	//
	// alertDialogBuilder
	// .setMessage(realBusName() + parsedTweet)
	// .setCancelable(false)
	// .setPositiveButton("Okay",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int id) {
	// dialog.cancel();
	// }
	// });
	//
	// AlertDialog alertDialog = alertDialogBuilder.create();
	//
	// alertDialog.show();
	// }

	public void setBusName(String busName) {
		this.busName = busName;
	}

	public String getBusName() {
		return busName;
	}

	private class MyLocationListener implements LocationListener {
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(android.location.Location location) {
			System.out.println("Current location is: " + location.getLatitude()
					+ ", " + location.getLongitude());
			setLocation(location);
		}
	};

	public void setLocation(android.location.Location loc) {
		this.userLocation = loc;
	}

	public android.location.Location getLocation() {
		return this.userLocation;
	}

	public void setUserBusStop(String busStop) {
		this.userBusStop = busStop;
	}

	public String getUserBusStop() {
		return this.userBusStop;
	}

	public int getBusRoutePosition() {
		return busRoutePosition;
	}

	public String realBusName() {
		String temp = getBusName();
		temp = temp.replace("_", " ");
		return temp;
	}

	public void setBusRoutePosition(int spinnerPosition) {
		busRoutePosition = spinnerPosition;
	}

	private class GetTweet extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... params) {
			return getTweets(params[0]);
		}

		public String[] getTweets(String userPosition) {
//			Twitter twitter = new TwitterFactory().getInstance();
			try {
				List<twitter4j.Status> tweets = TwitterUtils.getTweet(prefs, new Query("%23BT" + getBusName())).getTweets();

				if (tweets.size() > 0) {
					int usersbusstop = bT.mapper.busRoutes.get(
							getBusRoutePosition()).findStopUsingStopCode(
							userPosition);
					int position = usersbusstop;

					while (position != usersbusstop + 1) {

						if (position == 0) {
							position = bT.mapper.busRoutes
									.get(getBusRoutePosition()).stops.size() - 1;

						} else {
							position--;

						}

						String toQuery = bT.mapper.busRoutes.get(
								getBusRoutePosition()).getNearestBus(position);
						for (twitter4j.Status tweet : tweets) {

							String[] parsedTweet = tweet.getText().split(" ");
							System.out.println(parsedTweet[3]);
							if (parsedTweet[3].equals(toQuery)) {

								return parsedTweet;
							}
						}
					}
				}

				else {
					return null;
				}

			} catch (Exception te) {
				te.printStackTrace();
				System.out.println("Failed to search tweets: "
						+ te.getMessage());
				System.exit(-1);
			}
			return null;
		}
	}

	private class SendTweet extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			sendTweet(params[0], params[1], params[2]);
			return "";
		}
	}

	public void sendTweet(String busName, String busStop, String time) {
		try {
			TwitterUtils.sendTweet(prefs, "#BT" + busName + " was at "
					+ busStop + " at " + time);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public QueryResult getTweet(Query query) {
		try {
			return TwitterUtils.getTweet(prefs, query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private class BackUp extends AsyncTask<String, Void, String> {
		ArrayList<String> stopName = new ArrayList<String>();

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();

			// Handle timeout
			final HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 3000);
			// End timeout handling

			HttpGet get = new HttpGet(
					"http://www.bt4u.org/webservices/bt4u_webservice.asmx/"
							+ "GetNextDepartures?routeShortName=" + params[0]
							+ "&stopCode=" + params[1]);

			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();

				SAXParserFactory sxf = SAXParserFactory.newInstance();
				SAXParser sp = sxf.newSAXParser();
				DefaultHandler handler = new DefaultHandler() {
					boolean patternPoint = false;
					boolean adjTime = false;
					String test = "";

					public void startElement(String uri, String localName,
							String qName, Attributes attributes)
							throws SAXException {

						if (qName.equals("StopName")) {
							patternPoint = true;
						}

						if (qName.equals("AdjustedDepartureTime")) {
							adjTime = true;
						}

					}

					public void endElement(String uri, String localName,
							String qName) throws SAXException {

					}

					public void characters(char ch[], int start, int length)
							throws SAXException {

						if (patternPoint) {
							test = new String(ch, start, length);
							if (!test.equals("")) {
								// stopName.add(test);
								// System.out.println(test);
							}

							patternPoint = false;

						}

						if (adjTime) {
							String test2 = new String(ch, start, length);
							if (!test.equals("")) {
								stopName.add(test2);

							}

							adjTime = false;
						}

					}

				};

				InputStream stream = entity.getContent();
				sp.parse(stream, handler);
				stream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (stopName.size() > 0) {
				return stopName.get(0);
			} else {
				return null;
			}

		}
	}

}
