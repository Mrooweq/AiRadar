package com.airadar.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.airadar.R;

public class Algorithm
{
	private static final String URL = "https://data-live.flightradar24.com/zones/fcgi/feed.js?bounds=";
	private static final int GEO_FACTOR = 2;
	private static final double SPEED_MULTPLIER = 1.852;
	private static final double ALTITUDE_MULTPLIER = 3.28084;

	private static Hashtable<String, Plane> closePlanes = new Hashtable<String, Plane>();
	private static ArrayList<String> closestPlanes = new ArrayList<String>();
	private Location ourLocation = new Location("ourLocation");
	private Location planeLocation = new Location("planeLocation");
	private boolean isClosestPlaneChosen = false;
	private CameraScreen cameraScreen;

	private double distance;
	private int altitude;
	private int azimuth;
	private double track;
	private double speed;
	private static String lineCode;
	private static String from;
	private static String to;
	private double oldAngle;

	public Plane getAngle(String minName, int step)
	{
		double newAngle = 0;
		Plane plane = null;

		try {
			plane = closePlanes.get(minName);
			lineCode = plane.getLineCode();
			altitude = plane.getAltitude();
			azimuth = plane.getAzimuth();
			track = plane.getTrack();
			speed = plane.getSpeed();
			from = plane.getFrom();
			to = plane.getTo();

			double way = plane.getSpeed() * 0.001 / 3.6 * step;
			double b = plane.getDistance() * Math.sin(Math.toRadians(plane.getAzimuth()));
			double a = plane.getDistance() * Math.cos(Math.toRadians(plane.getAzimuth()));

			double c = Math.sin(Math.toRadians(plane.getTrack())) * way;
			double d = Math.cos(Math.toRadians(plane.getTrack())) * way;

			double tan = 0;
			double x = b + c;
			double y = a + d;

			if (x > 0 && y > 0)  // 0 - 90 degrees
			{
				tan = x / y;
				newAngle = Math.toDegrees(Math.atan(tan));
			}
			else if (x > 0 && y < 0)  // 91 - 180 degrees
			{
				tan = x / y;
				newAngle = Math.toDegrees(Math.atan(tan));
				newAngle = 180 + newAngle;
			}
			else if (x < 0 && y < 0)  // 181 - 270 degrees
			{
				tan = x / y;
				newAngle = Math.toDegrees(Math.atan(tan));
				newAngle = 180 + newAngle;
			}
			else if (x < 0 && y > 0)  // 271 - 360 degrees
			{
				tan = x / y;
				newAngle = Math.toDegrees(Math.atan(tan));
				newAngle = 360 + newAngle;
			}

			distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
			plane.setLineCode(lineCode);

			if (newAngle < 0)
				newAngle = newAngle + 360;

			oldAngle = newAngle;
			plane.setAngle(newAngle);

		} catch (NullPointerException e)
		{
			e.printStackTrace();
			newAngle = oldAngle;
		}
		catch (ArithmeticException e) {
			e.printStackTrace();
		};

		return plane;
	}


	public String getPlanesData(SharedPreferences sharedpreferences) throws LackOfConnection
	{
		String responseBody = null, from="", to="", lineCode="", callsign = "", currentPlaneCallsign="";
		long dist, minDistance, track;
		int azimuth, count, altitude;
		double latitude, longitude, speed;

		float plat = sharedpreferences.getFloat(MainScreen.LATITUDE_PREF, 0);
		float plong = sharedpreferences.getFloat(MainScreen.LONGITUDE_PREF, 0);

		ourLocation.setLatitude(plat);
		ourLocation.setLongitude(plong);

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(getUrl(plat, plong, GEO_FACTOR));
		HttpResponse response = null;

		try {
			response = httpclient.execute(httpget);
		}
		catch (ClientProtocolException e1) {}
		catch (IOException e1)		{
			throw new LackOfConnection();
		}

		try {
			responseBody = EntityUtils.toString(response.getEntity());
		}
		catch (ParseException e1) {}
		catch (IOException e1) {}


		////////////////////////////////////////////

		JSONObject json;
		JSONArray names = null;
		JSONArray values = null;

		try
		{
			json = new JSONObject(responseBody);
			names = json.names();
			values = json.toJSONArray(names);
		} catch (JSONException e1) {}

		count = values.length();
		minDistance = MainScreen.maxDistance;

		for (int i=0; i<count; i++)
		{
			try
			{
				latitude = values.getJSONArray(i).getDouble(1);
				longitude = values.getJSONArray(i).getDouble(2);
				track = (long)values.getJSONArray(i).getDouble(3);
				altitude = (int) Math.rint(values.getJSONArray(i).getDouble(4) / ALTITUDE_MULTPLIER);
				speed = (double)Math.round(values.getJSONArray(i).getDouble(5) * SPEED_MULTPLIER);
				from = values.getJSONArray(i).getString(11);
				to = values.getJSONArray(i).getString(12);
				callsign = values.getJSONArray(i).getString(16);

				planeLocation.setLatitude(latitude);
				planeLocation.setLongitude(longitude);

				dist = (long)ourLocation.distanceTo(planeLocation);
				lineCode = names.getString(i).substring(0,3);

				if (dist < MainScreen.maxDistance)
				{
					azimuth = (int)ourLocation.bearingTo(planeLocation);
					closePlanes.put(callsign, new Plane(planeLocation, dist, speed, track, azimuth, altitude, from, to, lineCode));

					if (!closestPlanes.contains(callsign))
						closestPlanes.add(callsign);

					if(!isClosestPlaneChosen)
					{
						if (dist < minDistance && altitude != 0)
						{
							minDistance = dist;
							currentPlaneCallsign = callsign;
						}
					}
				}
			}
			catch (JSONException e)			{
				e.printStackTrace();
			}
		}
	if(!currentPlaneCallsign.equals(""))
		isClosestPlaneChosen = true;

		return currentPlaneCallsign;
	}

	public String loadAirports(Context ctx)
	{
		int i;
		InputStream inputStream;
		ByteArrayOutputStream byteArrayOutputStream;

		inputStream = ctx.getResources().openRawResource(R.raw.airports);
		byteArrayOutputStream = new ByteArrayOutputStream();

		try
		{
			i = inputStream.read();
			while (i != -1)
			{
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	public String loadCallsignals(Context ctx)
	{
		int i;
		InputStream inputStream;
		ByteArrayOutputStream byteArrayOutputStream;

		inputStream = ctx.getResources().openRawResource(R.raw.callsignals);
		byteArrayOutputStream = new ByteArrayOutputStream();

		try
		{
			i = inputStream.read();
			while (i != -1)
			{
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

	private String getUrl(float plat, float plong, int factor)
	{
		return  URL
				+ String.valueOf(plat + factor) + ","
				+ String.valueOf(plat - factor)+ ","

				+ String.valueOf(plong - factor) + ","
				+ String.valueOf(plong + factor);
	}

	public void clearPlanes(){
		closestPlanes.clear();
		closePlanes.clear();
		((CameraScreen)cameraScreen).nullifyText();
	}

	public void setClosestPlaneNotChosen()
	{
		isClosestPlaneChosen = false;
	}

	public void setCameraScreen(CameraScreen cameraScreen){
		this.cameraScreen = cameraScreen;
	}

	public ArrayList<String> getClosestPlanes(){
		return closestPlanes;
	}

	public String getTo(){
		return to;
	}

	public String getFrom(){
		return from;
	}

	public String getLineCode(){
		return lineCode;
	}

	public double getSpeed(){
		return speed;
	}

	public double getTrack(){
		return track;
	}

	public int getAzimuth(){
		return azimuth;
	}

	public int getAltitude(){
		return altitude;
	}

	public double getDistance(){
		return distance;
	}
}
