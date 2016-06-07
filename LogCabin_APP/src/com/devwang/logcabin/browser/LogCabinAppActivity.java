package com.devwang.logcabin.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.devwang.logcabin.R;

public class LogCabinAppActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.webbrowser_web);

		// WebView wv = (WebView) findViewById(R.id.wv);
		// wv.loadUrl("http://devwang.sinaapp.com/LogCabin/APP/LogCabin_APP.apk");

		if (isNetworkConnected(this)) {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri
					.parse("http://devwang.sinaapp.com/LogCabin/APP/LogCabin_APP.apk");
			intent.setData(content_url);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(),
					"亲！您的手机未开启网络哦，请打开网络再访问，Wifi也OK~", Toast.LENGTH_SHORT)
					.show();
			LogCabinAppActivity.this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogCabinAppActivity.this.finish();
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
