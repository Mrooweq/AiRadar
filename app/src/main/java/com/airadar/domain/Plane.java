package com.airadar.domain;

import android.location.Location;

public class Plane
{
	public static final double DISTANCE_MULTIPLIER = 0.001;

	private double distance;
	private double speed;
	private long track;
	private int azimuth;
	private int altitude;
	private String from;
	private String to;
	private String lineCode;
	private double angle;

	public String getTo(){
		return to;
	}

	public String getLineCode(){
		return lineCode;
	}

	public String getFrom(){
		return from;
	}

	public int getAltitude(){
		return altitude;
	}

	public long getTrack(){
		return track;
	}

	public int getAzimuth(){
		return azimuth;
	}

	public double getSpeed(){
		return speed;
	}

	public double getDistance(){
		return distance;
	}

	public double getAngle(){
		return angle;
	}



	public void setAngle(double kat){
		this.angle = kat;
	}

	public void setLineCode(String skrotPrzewoznika){
		this.lineCode = skrotPrzewoznika;
	}


	public Plane(Location plane_location, long distance, double speed, long track, int azimuth, int altitude, String from, String to, String lineCode)
	{
		this.distance = distance * DISTANCE_MULTIPLIER;
		this.speed = speed;
		this.track = track;
		this.azimuth = azimuth;
		this.altitude = altitude;
		this.from = from;
		this.to = to;
		this.lineCode = lineCode;
	}
}