package com.example.portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.example.portal.LoginActivity;
import com.example.portal.MailList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final String TAG = "MainActivity";
	
	protected SharedPreferences  settings;
	Button login, go;
	EditText account;
	EditText password;
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		
		findViews();
		setListener();
	}

	private void findViews(){
		login = (Button)findViewById(R.id.button_login);
		account = (EditText)findViewById(R.id.account);
		password = (EditText)findViewById(R.id.password);
	}

	private void setListener(){
		login.setOnClickListener(l);
	}


	OnClickListener l = new OnClickListener(){
		public void onClick(View view){
			new LoginTask().execute();	
		}
	};
	
	private boolean login(String username, String password){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://118.163.49.158:8080/Portal/login");
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		HttpContext context = new BasicHttpContext(); /* for retrive cookie */
		CookieStore cookieStore = new BasicCookieStore();
		InputStream inputStream = null;
		String result = "";
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = httpClient.execute(httpPost,context);
			if(httpResponse.getStatusLine().getStatusCode() == 200)
			{
				Log.e(TAG,"http request success");
				httpEntity = httpResponse.getEntity();
				inputStream = httpEntity.getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

				StringBuilder builder = new StringBuilder();
				String line = null;

				while( (line = bufferedReader.readLine()) != null ){
					builder.append(line + "\n");
				}
				inputStream.close();
				bufferedReader.close();
				result = builder.toString();

				List<Cookie> cookies = cookieStore.getCookies();
				Log.e(TAG, "session: "+cookies);

				if (!cookies.isEmpty()) {
					for (int i = cookies.size(); i > 0; i --) {
						Cookie cookie = (Cookie) cookies.get(i - 1);
						if (cookie.getName().equalsIgnoreCase("PLAY_SESSION")) {
							// store cookie for session sharing with webview and browser
							Utilty.appCookie = cookie;
//							Log.e(TAG, "exist? "+cookie.getValue().indexOf("portalUser"));
							
							if(cookie.getValue().indexOf("portalUser")>0)
								return true;
							else
								return false;
						}
						else{
							Log.e(TAG, "NO!!");
						}
					}
				}
			}
			else{
				Log.e(TAG,"http request error");
			}

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public class LoginTask extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			if(login(account.getText().toString(), password.getText().toString()))
				return true;
			else
				return false;
		}
		protected void onPostExecute(Boolean result){
			if(result){
				Log.e("may_log", ""+readLogin_status());
				saveLogin_status();
				Log.e("may_log", ""+readLogin_status());
//				Utilty._signal = true;
				Bundle bundle = new Bundle();
				Intent intent = new Intent();
				intent.putExtras(bundle);
				intent.setClass(LoginActivity.this, MailList.class);
				startActivity(intent);
			}
			else{
				Toast t = Toast.makeText(LoginActivity.this, "Account or Password error", Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER, 12, 100);
				t.show();
				Log.e(TAG,"log in error");
			}
		}


	}
	
	public void saveLogin_status(){
		settings = getSharedPreferences(Utilty._login,0);
		settings.edit().putBoolean(Utilty._login_status, false).commit();
		
	}
	public void saveLogin_status2(){
		settings = getSharedPreferences(Utilty._login,0);
		settings.edit().putBoolean(Utilty._login_status, true).commit();
		
	}
	private boolean readLogin_status(){
		settings = getSharedPreferences(Utilty._login,0);
		return settings.getBoolean(Utilty._login_status, true);
	}
	
	
}
