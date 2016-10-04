package com.airadar.domain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airadar.R;

public class CustomDialog {

	public void showNoConnectionAlert(final Context ctx, String msg)
	{
		AlertDialog noInternetAlert = new AlertDialog.Builder(ctx).create();
		noInternetAlert.setMessage(msg);

		noInternetAlert.setButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {}
		});

		noInternetAlert.show();
	}

	public static AlertDialog setCoordinatestDialog(final Context ctx, final SharedPreferences sharedpreferences, final Algorithm algorithm)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

		TextView currentLat = new TextView(ctx);
		TextView currentLong = new TextView(ctx);
		final EditText longEditText = new EditText(ctx);
		final EditText latEditText = new EditText(ctx);

		latEditText.setHint(ctx.getString(R.string.latitude_hint));
		longEditText.setHint(ctx.getString(R.string.longitude_hint));
		longEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		latEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		longEditText.setEms(14);
		latEditText.setEms(14);

		LinearLayout ll3 = new LinearLayout(ctx);
		ll3.setOrientation(1);
		ll3.addView(currentLat);
		ll3.addView(currentLong);

		LinearLayout ll1 = new LinearLayout(ctx);
		ll1.addView(latEditText);

		LinearLayout ll2 = new LinearLayout(ctx);
		ll2.addView(longEditText);

		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(1);
		ll.addView(ll3);
		ll.addView(ll1);
		ll.addView(ll2);

		alert.setView(ll);
		alert.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String strLat = String.valueOf(latEditText.getText());
				String strLong = String.valueOf(longEditText.getText());

				if (areValuesValid(strLat, strLong))
				{
					float plat = Float.valueOf(strLat);
					float plong = Float.valueOf(strLong);

					Editor editor = sharedpreferences.edit();
					editor.putFloat(MainScreen.LATITUDE_PREF, plat);
					editor.putFloat(MainScreen.LONGITUDE_PREF, plong);
					editor.commit();

					algorithm.clearPlanes();
									}
				else
					Toast.makeText(ctx, ctx.getString(R.string.inserted_values_arent_correct), Toast.LENGTH_SHORT).show();

			}});

		alert.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		return alert.create();
	}

	private static boolean areValuesValid(String strLat, String strLong)
	{
		boolean areValid = false;

		try
		{
			float plat = Float.valueOf(strLat);
			float plong = Float.valueOf(strLong);

			if(plat < -90 || plat > 90 || plong < -180 || plong > 180)
				areValid = false;
			else
				areValid = true;
		}
		catch(NumberFormatException e)	{
			areValid = false;
		}

		return areValid;
	}


	public static AlertDialog setMaxDistanceDialog(final Context ctx, final SharedPreferences sharedpreferences, final Algorithm algorithm)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);

		final EditText maxDistaceET = new EditText(ctx);
		maxDistaceET.setHint(ctx.getString(R.string.max_distance_for_planes));
		maxDistaceET.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		maxDistaceET.setEms(14);

		LinearLayout ll = new LinearLayout(ctx);
		ll.addView(maxDistaceET);

		alert.setView(ll);
		alert.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				String maxDistanceStr = String.valueOf(maxDistaceET.getText());

				try{
					long maxDistance = Long.parseLong(maxDistanceStr);

					MainScreen.maxDistance = maxDistance;
					Editor editor = sharedpreferences.edit();
					editor.putLong(MainScreen.MAX_DISTANCE__PREF, maxDistance);
					editor.commit();

					algorithm.clearPlanes();

				} catch(NumberFormatException e){
					Toast.makeText(ctx, ctx.getString(R.string.inserted_values_isnt_correct), Toast.LENGTH_SHORT).show();
				}
			}});

		alert.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
			}
		});
		return alert.create();
	}

}
