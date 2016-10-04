package com.airadar.domain;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airadar.R;
import com.airadar.module.MyApp;

import javax.inject.Inject;

public class CameraScreen extends Activity implements SurfaceHolder.Callback
{
	@Inject
	Algorithm algorithm;

	@Inject
	CustomDialog customDialog;

	@Inject
	SharedPreferences sharedpreferences;

	private static final double SMALL_NUMBER = 0.0000001;
	private static final int WIDTH_SPACE = 67;
	private static final int HEIGHT_SPACE = 15;
	private static final int GRAY = Color.argb(180, 229, 229, 229);
	private static final int REFRESHING_INTERVAL = 20000;
		
	private Camera mCamera;
	private boolean mPreviewRunning = false;
	private SensorManager mSensorManager;
	private int planeIndex = 0;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Plane plane;
	
	private String airportsStr;
	private String callsignalStr;
	private String skrotPrzewoznika;
	private String carrier;
	private double angle;
	private static int step = 0;
	private static String currentPlaneCallsign = "";
	private boolean isToastShown = false;
	private boolean isMoreInfoShown = true;
	private TextView fromToInfo;
	
	private TextView generalInfoTextView;
	private Button nextButton;
	private Button moreButton;
	private LinearLayout infoLinearLayout;
	private ImageView arrow;
	private ImageView pointer;

	private Timer timer;
	private Timer timer2;


	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		((MyApp) getApplication()).getComponent().inject(this);

		algorithm.setCameraScreen(this);

		Intent intent = getIntent();
		airportsStr = intent.getStringExtra(MainScreen.AIRPLANES);
		callsignalStr = intent.getStringExtra(MainScreen.CALLSIGNALS);
		currentPlaneCallsign = intent.getStringExtra(MainScreen.CURRENT_PLANE_CALLSIGNAL);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		mSurfaceView = (SurfaceView)findViewById(R.id.surface_camera);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);	

		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(mListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

		showPlanes();
	}	

	private void showPlanes()
	{
		setTimers();
		setUI();
	}
	
	private void setUI()
	{
		nextButton = (Button)findViewById(R.id.next_btn);
		moreButton = (Button)findViewById(R.id.more_btn);
		fromToInfo = (TextView)findViewById(R.id.from_to_info);
		
		infoLinearLayout = (LinearLayout)findViewById(R.id.layout_with_TV);
		arrow = (ImageView)findViewById(R.id.arrow);
		pointer = (ImageView)findViewById(R.id.pointer);
		
		nextButton.setOnClickListener(setNextButtonListener());
		moreButton.setOnClickListener(setMoreButtonListener());
		
		generalInfoTextView = (TextView)findViewById(R.id.general_info);
		generalInfoTextView.setTextColor(Color.RED);
	}
	
	private void setTimers()
	{
		timer = new Timer();
		TimerTask tt = new TimerTask()
		{
			@Override
			public void run()
			{
				plane = algorithm.getAngle(currentPlaneCallsign, step);

				if (plane != null)
				{
					skrotPrzewoznika = plane.getLineCode();
					angle = plane.getAngle();
				}

				step++;
			}
		};
		timer.schedule(tt, 0, 1000);


		timer2 = new Timer();		        
		TimerTask tt2 = new TimerTask()
		{
			@Override
			public void run()
			{

				try {
					String planeCallsign = algorithm.getPlanesData(sharedpreferences);

					if(!planeCallsign.equals("") && !planeCallsign.equals(currentPlaneCallsign))
						currentPlaneCallsign = planeCallsign;

					step = 0;
				} catch (LackOfConnection e) {

					timer.cancel();
					timer2.cancel();

					Intent intent = new Intent(CameraScreen.this, MainScreen.class);
					intent.putExtra(MainScreen.NAME, MainScreen.VALUE);
					startActivity(intent);
				}
			}
		};
		timer2.schedule(tt2, REFRESHING_INTERVAL, REFRESHING_INTERVAL);
	}
	
	private OnClickListener setMoreButtonListener()
	{
		return new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{	
				if (isMoreInfoShown)
				{
					isMoreInfoShown = false;
					generalInfoTextView.setTextColor(Color.TRANSPARENT);
					((Button)v).setText(getString(R.string.more_info));
				}
				else 
				{
					isMoreInfoShown = true;
					generalInfoTextView.setTextColor(Color.RED);
					((Button)v).setText(getString(R.string.less_info));
				}

			}
		};
	}
	
	private OnClickListener setNextButtonListener()
	{
		return new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if (algorithm.getClosestPlanes().size() > 0)
				{
					int size = algorithm.getClosestPlanes().size();

					planeIndex++;

					if (planeIndex == size)
						planeIndex = 0;

					if (algorithm.getClosestPlanes().size() > 0)
						currentPlaneCallsign = algorithm.getClosestPlanes().get(planeIndex);
				}

			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settingmanual:
			customDialog.setCoordinatestDialog(this, sharedpreferences, algorithm).show();
			break;
			case R.id.settingmaxdistance:
				customDialog.setMaxDistanceDialog(this, sharedpreferences, algorithm).show();
				break;
		}
		return true;
	}



	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}


	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
	{
		if (mPreviewRunning)
			mCamera.stopPreview();

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(w, h);
		mCamera.setParameters(p);

		try 
		{
			mCamera.setPreviewDisplay(holder);
		}
		catch (IOException e) 		{
			e.printStackTrace();
		}

		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	private final SensorEventListener mListener = new SensorEventListener() 
	{
		float[] inR = new float[16];
		float[] I = new float[16];
		float[] gravity = new float[3];
		float[] geomag = new float[3];
		float[] orientVals = new float[3];

		double azimuth = 0;
		double roll = 0;

		public void onSensorChanged(SensorEvent event) 
		{          		
			if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
				return;

			switch (event.sensor.getType()) {  
			case Sensor.TYPE_ACCELEROMETER:
				gravity = event.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				geomag = event.values.clone();
				break;
			}

			if (gravity != null && geomag != null) {

				boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
				if (success) {
					SensorManager.getOrientation(inR, orientVals);
					azimuth = Math.toDegrees(orientVals[0]);
					roll = Math.toDegrees(orientVals[2]);

					azimuth = (azimuth + 180) % 360;
					roll = (roll + 180) % 360;
				}
			}


			if (algorithm.getClosestPlanes().size() == 0)
			{			
				infoLinearLayout.setBackgroundColor(Color.TRANSPARENT);
				arrow.setVisibility(View.INVISIBLE);
				pointer.setVisibility(View.INVISIBLE);

				if(!isToastShown)
				{
					isToastShown = true;
					Toast.makeText(getApplicationContext(), getString(R.string.no_planes_detected), Toast.LENGTH_LONG).show();
				}
				
				return;
			}
			else  
			{
				if(!isToastShown)
				{
					isToastShown = true;

					if (algorithm.getClosestPlanes().size() != 1)
						Toast.makeText(getApplicationContext(),algorithm.getClosestPlanes().size() + " " + getString(R.string.plane_has_been_detected), Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getApplicationContext(),algorithm.getClosestPlanes().size() + " " + getString(R.string.plane_has_been_detected), Toast.LENGTH_LONG).show();
				}
			}

			int halfScreenWidth = mSurfaceView.getWidth() / 2 - 40;
			int halfScreenHeight = mSurfaceView.getHeight() / 2 - 12;
			double width = 0;
			double height = 0;
			double right_width = mSurfaceView.getWidth() - 53;
			double bottom_height = mSurfaceView.getHeight() - 53;
			double angle_opposite = 0;

			int altitude = algorithm.getAltitude();
			double distance = algorithm.getDistance();

			if (distance == 0)
				distance = SMALL_NUMBER;

			double tan = altitude / (distance * 1000);
			double heightAngle = Math.toDegrees(Math.atan(tan));

			height = halfScreenHeight - (640/37.8) * (heightAngle - (90 - roll));
			if (roll > 270 && roll <= 360)
				height = halfScreenHeight + (640/37.8) * (heightAngle - (90 - (361-roll)));

			if (angle >= 360)
				angle = angle - 360;

			if (height < -20)
				height = 0;
			if (height > bottom_height)
				height = bottom_height;							

			double value = azimuth - 90;
			if (value < 0)
				value = value + 360;

			width = halfScreenWidth - (480/54.4) * (value - angle);  // 480/54.4

			double width2 = width;

			if (angle > 180)
				angle_opposite = angle - 180;
			else 
				angle_opposite = angle + 180;

			if (angle <= 180)
			{	
				if ((value >= angle && value < angle_opposite))
					width = 0;
				if ((value <= angle || value > angle_opposite))
					width = right_width;
				if (Math.abs(value - angle) < 28)
				{
					width = width2;

				}
			}

			if (angle > 180)
			{		
				if ((value >= angle || value < angle_opposite))
					width = 0;
				if ((value <= angle && value > angle_opposite))
					width = right_width;
				if (Math.abs(value - angle) < 28)
				{
					width = width2;
				}
			}
			
			if (height == 0)
				arrow.setImageResource(R.drawable.top);
			if (height == bottom_height)
				arrow.setImageResource(R.drawable.bottom);
			if (width == 0)
				arrow.setImageResource(R.drawable.left);
			if (width == right_width)
				arrow.setImageResource(R.drawable.right);
			if (height == 0 && width == 0)
				arrow.setImageResource(R.drawable.top_left);
			if (height == bottom_height && width == 0)
				arrow.setImageResource(R.drawable.bottom_left);
			if (width == right_width && height == 0)
				arrow.setImageResource(R.drawable.top_right);
			if (width == right_width && height == bottom_height)
				arrow.setImageResource(R.drawable.bottom_right);

			arrow.setPadding((int)width, (int)height, 0, 0);

			String from;
			String to;

			try{  
				from = airportsStr.substring(0,airportsStr.indexOf(algorithm.getFrom()) - 1).substring(airportsStr.substring(0,airportsStr.indexOf(algorithm.getFrom()) - 1).lastIndexOf(")") + 3);
			} catch (Exception e){
				from = "-";
			}

			try{  
				to = airportsStr.substring(0,airportsStr.indexOf(algorithm.getTo()) - 1).substring(airportsStr.substring(0,airportsStr.indexOf(algorithm.getTo()) - 1).lastIndexOf(")") + 3);
			} catch (Exception e){
				to = "-";
			}


			try{  
				carrier = callsignalStr.substring(callsignalStr.indexOf(skrotPrzewoznika)+4).substring(0, callsignalStr.substring(callsignalStr.indexOf(skrotPrzewoznika)+4).indexOf(";"));
			} catch (Exception e){
				carrier = "-";
			}

			fromToInfo.setText("F: " + from +   
					"\nT: " + to );

			LinearLayout containerLayout = (LinearLayout)findViewById(R.id.container_ll);
			containerLayout.setPadding(arrow.getPaddingLeft() + WIDTH_SPACE, arrow.getPaddingTop() + HEIGHT_SPACE, 0, 0);
			
			int pointerAngle = (int) algorithm.getTrack() - algorithm.getAzimuth();
			if (pointerAngle < 0)
				pointerAngle = pointerAngle + 360;
			
			if (width != 0 && width != right_width && height != 0 && height != bottom_height)
			{
				arrow.setImageResource(0);
				pointer.setPadding(arrow.getPaddingLeft() + 37, arrow.getPaddingTop() + 15, 0, 0);	
				fromToInfo.setTextColor(Color.BLACK);
				infoLinearLayout.setBackgroundColor(GRAY);
				pointer.setVisibility(View.VISIBLE);
			}
			else
			{
				fromToInfo.setTextColor(Color.TRANSPARENT);
				infoLinearLayout.setBackgroundColor(Color.TRANSPARENT);
				pointer.setVisibility(View.INVISIBLE);
			}

			Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.pointer);
			Matrix mat = new Matrix();
			mat.postRotate(pointerAngle);
			Bitmap bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), mat, true);
			pointer.setImageBitmap(bMapRotate);
			
			if (currentPlaneCallsign != "")		    	
				generalInfoTextView.setText("callsign: " + currentPlaneCallsign + "\ndistance: " + algorithm.getDistance() + "[km]" + "\naltitude: " + algorithm.getAltitude() + "[m]" + "\nspeed: " + algorithm.getSpeed() + "[km/h]" + "\nairline: " + carrier);
			else
				generalInfoTextView.setText("");
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};

	public void nullifyText(){
		generalInfoTextView.setText("");
	}

}
