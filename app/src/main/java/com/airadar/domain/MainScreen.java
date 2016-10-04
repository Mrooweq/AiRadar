package com.airadar.domain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.airadar.R;
import com.airadar.module.MyApp;

import javax.inject.Inject;

public class MainScreen extends Activity
{
	@Inject
	Algorithm algorithm;

	@Inject
	CustomDialog customDialog;

	@Inject
	SharedPreferences sharedpreferences;

	private Handler handler;
	private TextView pleaseWaitTextView;
	private Button startTrackingButton;

	public static final String NAME = "name";
	public static final String VALUE = "connectionLost";
	public static final String AIRPLANES = "AIRPLANES";
	public static final String CALLSIGNALS = "CALLSIGNALS";
	public static final String CURRENT_PLANE_CALLSIGNAL = "CURRENT_PLANE_CALLSIGNAL";

	public static final String LATITUDE_PREF = "lat";
	public static final String LONGITUDE_PREF = "long";
	public static final String MAX_DISTANCE__PREF = "maxDistance";

	public static long maxDistance;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		((MyApp) getApplication()).getComponent().inject(this);

		maxDistance = sharedpreferences.getLong(MAX_DISTANCE__PREF, 50000);

		pleaseWaitTextView = (TextView)findViewById(R.id.please_wait);
		startTrackingButton = (Button)findViewById(R.id.start_trackng);
		handler = new Handler();

		startTrackingButton.setOnClickListener(setStartTrackingButtonListener());

		float plat = sharedpreferences.getFloat(LATITUDE_PREF, 0);
		float plong = sharedpreferences.getFloat(LONGITUDE_PREF, 0);

		if(plat == 0 && plong == 0)
			customDialog.setCoordinatestDialog(this, sharedpreferences, algorithm).show();

		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null && extras.getString(NAME).equals(VALUE)) {
				customDialog.showNoConnectionAlert(MainScreen.this, getString(R.string.connection_lost));
			}
		}
	}

	private OnClickListener setStartTrackingButtonListener()
	{
		return new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				pleaseWaitTextView.setText(getString(R.string.planes_detecting));
				new Thread(new Runnable()
				{
					public void run()
					{
						String airportsStr = algorithm.loadAirports(MainScreen.this);
						String callsignalsStr = algorithm.loadCallsignals(MainScreen.this);

						try{
							String currentPlaneCallsign = algorithm.getPlanesData(sharedpreferences);

							Intent intent = new Intent(MainScreen.this, CameraScreen.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
							intent.putExtra(AIRPLANES, airportsStr);
							intent.putExtra(CALLSIGNALS, callsignalsStr);
							intent.putExtra(CURRENT_PLANE_CALLSIGNAL, currentPlaneCallsign);

							startActivity(intent);
						} catch (LackOfConnection e) {
							MainScreen.this.runOnUiThread(new Runnable() {
								public void run() {
									customDialog.showNoConnectionAlert(MainScreen.this, getString(R.string.internet_connection_is_necessary));
								}
							});
						}

						handler.post(new Runnable()
						{
							@Override
							public void run()
							{
								pleaseWaitTextView.setText("");
							}
						});
					}
				}).start();
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

}