package com.example.portal;


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class WelcomeActivity extends Activity {
	protected SharedPreferences settings;
	private final String TAG = "WelcomeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		Log.e("may_wel", ""+readLogin_status());
		Intent intent = new Intent();
		if(readLogin_status())
			intent.setClass(WelcomeActivity.this, MailList.class);
		else
			intent.setClass(WelcomeActivity.this, LoginActivity.class);
		startActivity(intent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}
	
	/* 檢查是否已經login */
	private boolean readLogin_status(){
		settings = getSharedPreferences(Utilty._login,0);
		return settings.getBoolean(Utilty._login_status, false);
	}
	public void saveLogin_status2(){
		settings = getSharedPreferences(Utilty._login,0);
		settings.edit().putBoolean(Utilty._login_status, true).commit();
		
	}
	

}
