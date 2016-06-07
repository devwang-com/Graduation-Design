package com.devwang.logcabin.browser;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.devwang.logcabin.R;

public class LogCabinWebActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.webbrowser_web);
		WebView wv = (WebView) findViewById(R.id.wv);
		WebSettings wvSettings=wv.getSettings();
		wvSettings.setJavaScriptEnabled(true);
		// wv.loadUrl("http://devwang.sinaapp.com/LogCabin/LogCabin_Web.php");

		if (isNetworkConnected(this)) {
			wv.loadUrl("http://devwang.sinaapp.com/LogCabin/LogCabin_Web.php");
		} else {
			Toast.makeText(getApplicationContext(),
					"亲！您的手机未开启网络哦，请打开网络再访问，Wifi也OK~", Toast.LENGTH_SHORT)
					.show();
			LogCabinWebActivity.this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogCabinWebActivity.this.finish();
	}

	// 判断是否有网络连接
	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

}
