package com.example.portal;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
//import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class MailWebView extends Activity {
	
	WebView webview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
  
		Bundle bundle =this.getIntent().getExtras();
		String url = bundle.getString("url");
		
		findview();
		
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
		
		webview.loadUrl(url);
	}
	private void findview(){
		webview = (WebView)findViewById(R.id.mybrowser);
	}

}
