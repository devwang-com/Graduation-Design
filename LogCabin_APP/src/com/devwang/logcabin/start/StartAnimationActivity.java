package com.devwang.logcabin.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.devwang.logcabin.LogCabinMainActivity;
import com.devwang.logcabin.R;

public class StartAnimationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		Love love = new Love(this);  
        setContentView(love);  
		
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(StartAnimationActivity.this,
						PassWordActivity.class);
				startActivity(intent);
				StartAnimationActivity.this.finish();
			}
		}, 5000);
	}
}
