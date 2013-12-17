package com.example.portal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class Portal extends Activity {
	private final static String TAG = "Portal";
	protected ListView lv1;
//	protected ListView lv2;
//	private String s1[] = {"a", "b", "c", "d", "e", "f"};
//	private String s2[] = {"r", "s", "t", "u", "v", "w", "x"};
	protected String s3[];
	protected static String[] response = null;
	protected Bundle bundle = new Bundle();
	String urls[];
//	protected String jsonString; 
//	protected static ArrayAdapter<String> adapter1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_portal);
		
		findviews();
		/* Async task */
		new GetMailListTask().execute();

	}
	
	
	private class GetMailListTask extends AsyncTask<Void, Void, ArrayList<String>>{
		protected ArrayList<String> doInBackground(Void... params) {
			String result = getMailList();
			return convertJson(result);
		}
		protected void onPostExecute(ArrayList<String> result){
			Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
			Log.e(TAG,"size: "+result.size());
			ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Portal.this, android.R.layout.simple_list_item_1, result);
			lv1.setAdapter(adapter1);
//			Portal.response =  result;
			lv1.setTextFilterEnabled(true);
			lv1.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> a, View view,
						int pos, long id) {
					// TODO Auto-generated method stub
					Toast.makeText(Portal.this, "您選的是第"+pos+"個link", Toast.LENGTH_LONG).show();
					bundle.putString("url", urls[pos]);
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(Portal.this, MailWebView.class);
					startActivity(intent);
					
				}
			});
			Log.e(TAG,"74");
		}
	}
	private void findviews(){
		lv1 = (ListView)findViewById(R.id.list1);
//		lv2 = (ListView)findViewById(R.id.list2);
	}

	public String getMailList(){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://140.113.73.28/itri/getMailList.php");
		HttpResponse httpResponse = null;
		HttpEntity httpEntity = null;
		InputStream inputStream = null;
		String result= "";
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		/* for query string */
//		params.add(new BasicNameValuePair("query_string", "124"));
		/* for query string */
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = httpClient.execute(httpPost);
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
	
	
}
