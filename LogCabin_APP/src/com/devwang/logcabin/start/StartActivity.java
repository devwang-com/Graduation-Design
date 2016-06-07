package com.devwang.logcabin.start;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.devwang.logcabin.LogCabinMainActivity;
import com.devwang.logcabin.R;

public class StartActivity extends Activity{
	boolean isFirstIn = false;//是否第一次使用APP
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.start_main);
		
		//是否第一次使用APP
		SharedPreferences preferences = getSharedPreferences("first_pref",
				MODE_PRIVATE);
		isFirstIn = preferences.getBoolean("isFirstIn", true);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = null;
				if (isFirstIn) {
					//首次安装跳入引导界面
					intent = new Intent(StartActivity.this, StartPagesActivity.class);
				} else {
					//非首次安装进入开始界面
					intent = new Intent(StartActivity.this, LogCabinMainActivity.class);
				}
				StartActivity.this.startActivity(intent);
				StartActivity.this.finish();
			}
		}, 3000);
	}
}
