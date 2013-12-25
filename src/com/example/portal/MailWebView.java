package com.example.portal;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
//import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class MailWebView extends Activity {
	private final String TAG = "MailWebView";
	WebView webview;
	SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

		Bundle bundle =this.getIntent().getExtras();
		String url = bundle.getString("url");

		webview = new WebView(MailWebView.this);
		ViewGroup layout = (ViewGroup)findViewById(R.id.webviewLayout);

		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setBuiltInZoomControls(true);

		//		webview.getSettings().setDomStorageEnabled(true);

		/* 讓webview開起來的時候不會那麼大 */
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		/* 讓webview開起來的時候不會那麼大 */
		//		webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);

		webview.setWebViewClient(new WebViewClient());

		//		webview.setWebChromeClient(new WebChromeClient());
		Log.e("may_web", ""+readLogin_status());
		
		if(!readLogin_status()){
			//	CookieSyncManager.createInstance(this);
			CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
			CookieManager cookieManager = CookieManager.getInstance();
			Cookie sessionCookie = Utilty.appCookie;
			BasicClientCookie newCookie = new BasicClientCookie(Utilty.appCookie.getName(),Utilty.appCookie.getValue());
			Log.e(TAG,"cookie1 ==>"+ cookieManager.getCookie(url));

			if (newCookie != null) {
				cookieManager.removeSessionCookie();

				/* construct session */
				String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain=" + sessionCookie.getDomain() + "; path=" + sessionCookie.getPath() + "; expiry=null";

				cookieManager.setAcceptCookie(true);
				cookieManager.setCookie("http://118.163.49.158/Portal/", cookieString);
				Log.e(TAG, "name==>" + sessionCookie.getName());
				Log.e(TAG, "value==>" + sessionCookie.getValue());
				Log.e(TAG, "domain==>" + sessionCookie.getDomain());
				Log.e(TAG, "path==>" + sessionCookie.getPath());

				CookieSyncManager.getInstance().sync();
				
				saveLogin_status2();
			}
		}

		layout.addView(webview);
		webview.loadUrl(url);
	}
	
	private boolean readLogin_status(){
		settings = getSharedPreferences(Utilty._login,0);
		return settings.getBoolean(Utilty._login_status, true);
	}
	public void saveLogin_status2(){
		settings = getSharedPreferences(Utilty._login,0);
		settings.edit().putBoolean(Utilty._login_status, true).commit();
		
	}

}
