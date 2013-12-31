package com.example.portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;


public class MailList extends Activity {
	private final static String TAG = "Portal";
	protected ListView lv1;
	//	protected ListView lv2;
	private Button menu;
	private ImageButton set;
	//	private String s1[] = {"a", "b", "c", "d", "e", "f"};
	//	private String s2[] = {"r", "s", "t", "u", "v", "w", "x"};
	protected String s3[];
	protected static String[] response = null;
	protected Bundle bundle = new Bundle();
	String urls[];
	//	protected String jsonString; 
	//	protected static ArrayAdapter<String> adapter1;
	SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maillist);

		Log.e("wel2", ""+checkSessionExist());

		findviews();
		setListener();
		/* Async task */
		new GetMailListTask().execute();

	}


	public class GetMailListTask extends AsyncTask<Void, Void, ArrayList<String>>{
		protected ArrayList<String> doInBackground(Void... params) {
			String result = getMailList_POST();
//			String result = getMailList_GET();
			return convertJson(result);
		}
		protected void onPostExecute(ArrayList<String> result){
			Log.e(TAG,"size: "+result.size());
			ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MailList.this, android.R.layout.simple_list_item_1, result);
			lv1.setAdapter(adapter1);
			//			Portal.response =  result;
			lv1.setTextFilterEnabled(true);
			lv1.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> a, View view,
						int pos, long id) {
					//					Toast.makeText(MailList.this, "您選的是第"+pos+"個link", Toast.LENGTH_LONG).show();
					bundle.putString("url", urls[pos]);
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(MailList.this, MailWebView.class);
					startActivity(intent);

				}
			});
			Log.e(TAG,"74");
		}
	}
	private void findviews(){
		lv1 = (ListView)findViewById(R.id.list1);
		//		menu = (Button)findViewById(R.id.button_logout);
		set = (ImageButton)findViewById(R.id.set);
	}
	private void setListener(){
		set.setOnClickListener(j);
	}

	OnClickListener logoutListener = new OnClickListener(){
		public void onClick(View view){
			removeLogin_status();
			Intent intent = new Intent();
			intent.setClass(MailList.this, WelcomeActivity.class);
			startActivity(intent);
		}
	};
	OnClickListener j = new OnClickListener(){
		public void onClick(View view){
			showPopupmenu(view);
		}
	};

	public String getMailList_POST(){
		HttpClient httpClient = new DefaultHttpClient();
//		HttpPost httpPost = new HttpPost("http://140.113.73.28/itri/getMailList.php");
		HttpPost httpPost = new HttpPost("http://118.163.49.158:8080/Portal/Android_getMail");
		Log.e(TAG,"126");
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		InputStream inputStream = null;
		String result= "";

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		/* for query string */
		params.add(new BasicNameValuePair("Limit", "10"));
		/* for query string */
		
		if(Utilty.appCookie != null){
			String cookieString = Utilty.appCookie.getName() + "=" + Utilty.appCookie.getValue() + "; domain=" + Utilty.appCookie.getDomain() + "; path=" + Utilty.appCookie.getPath();
			Log.d(TAG, "Setting Cookie: "+Utilty.appCookie);
			httpPost.setHeader("Cookie", cookieString);
		} else {
			Log.i(TAG, "Null session request get()");
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = httpClient.execute(httpPost);
			Log.e(TAG,"response code: "+httpResponse.getStatusLine().getStatusCode());
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
				Log.e("http", "result: "+builder);
				inputStream.close();
				result = builder.toString();
			}
			else{
				//				inputStream.close();
				Log.e(TAG,"http request error");
			}

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			Log.e(TAG," 127 error");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG," 131 error");
			e.printStackTrace();
		}
		return result;
	}

	public String getMailList_GET() {
		String line;
		String result = "";
		String urlToRead = "http://118.163.49.158:8080/Portal/Android_getMail?Limit=10";
		StringBuilder builder = new StringBuilder();
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(urlToRead);
			// set cookie
			if(Utilty.appCookie != null){
				String cookieString = Utilty.appCookie.getName() + "=" + Utilty.appCookie.getValue() + "; domain=" + Utilty.appCookie.getDomain() + "; path=" + Utilty.appCookie.getPath();
				Log.d(TAG, "Setting Cookie: "+Utilty.appCookie);
				request.setHeader("Cookie", cookieString);
			} else {
				Log.i(TAG, "Null session request get()");
			}
			
			HttpResponse httpResponse = client.execute(request);

			if(httpResponse.getStatusLine().getStatusCode() == 200)
			{
				Log.e(TAG,"http GET request success");
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream inputStream = httpEntity.getContent();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

				builder = new StringBuilder();
				line = null;

				while( (line = bufferedReader.readLine()) != null ){
					builder.append(line + "\n");
				}
				Log.e(TAG, "GET result: "+builder);
				inputStream.close();
				result = builder.toString();
			}
//			Header[] headers = httpResponse.getAllHeaders();
//			for (int i=0; i < headers.length; i++) {
//				Header h = headers[i];
//				Log.i(TAG, "Header names: "+h.getName());
//				Log.i(TAG, "Header Value: "+h.getValue());
//			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		result = builder.toString();
		//	      Log.e(TAG,"GET result: " + result);
		return result;
	}

	private ArrayList<String> convertJson(String jsonString){
		ArrayList<String> result = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(jsonString);
			urls = new String[jsonArray.length()];
			for(int i = 0; i< jsonArray.length(); i++){
				JSONObject jsonData = jsonArray.getJSONObject(i);
				result.add(jsonData.get("Subject").toString());
				urls[i] = jsonData.get("HyperLink").toString();
				//				Log.e(TAG,"i ====> " + result[i]);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean checkSessionExist(){
		settings = getSharedPreferences(Utilty._login,0);
		return settings.getBoolean(Utilty._login_status, false);
	}

	public void removeLogin_status(){
		settings = getSharedPreferences(Utilty._login,0);
		settings.edit().remove(Utilty._login_status).commit();

	}
	private void logout(){
		removeLogin_status();
		Intent intent = new Intent();
	}

	private void showPopupmenu(View v){
		PopupMenu popupMenu = new PopupMenu(MailList.this, v);
		popupMenu.getMenuInflater().inflate(R.menu.option, popupMenu.getMenu());

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(MailList.this,
						item.toString(),
						Toast.LENGTH_LONG).show();

				if(item.toString().equals("Logout")){
					removeLogin_status();
					Intent intent = new Intent();
					intent.setClass(MailList.this, WelcomeActivity.class);
					startActivity(intent);
				}

				return true;
			}
		});

		popupMenu.show();
	}
}


