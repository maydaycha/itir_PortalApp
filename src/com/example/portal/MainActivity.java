package com.example.portal;

import com.example.portal.MainActivity;
import com.example.portal.Portal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	Button login;
	EditText account;
	EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViews();
		setListener();
	}

	private void findViews(){
		login = (Button)findViewById(R.id.button1);
		account = (EditText)findViewById(R.id.account);
		password = (EditText)findViewById(R.id.password);
	}

	private void setListener(){
		login.setOnClickListener(l);
	}


	OnClickListener l = new OnClickListener(){
		public void onClick(View view){
			Bundle bundle = new Bundle();
			bundle.putString("Account", account.getText().toString());
			bundle.putString("Password", password.getText().toString());
			
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(MainActivity.this, Portal.class);
			startActivity(intent);

		}
	};
}
