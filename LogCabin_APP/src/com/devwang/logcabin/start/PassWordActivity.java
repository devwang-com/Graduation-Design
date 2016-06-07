package com.devwang.logcabin.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devwang.logcabin.LogCabinMainActivity;
import com.devwang.logcabin.R;

public class PassWordActivity extends Activity {
	private Button ok_Button;
	private AutoCompleteTextView access;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.password_main);
		access = (AutoCompleteTextView) findViewById(R.id.et_path);
		String[] autoedit_items = { "logcabin" };
		access.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoedit_items));

		
		ok_Button = (Button) findViewById(R.id.btn_pass_in);
		ok_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("logcabin".equals(access.getText().toString().trim())) {
					Intent intent = new Intent(PassWordActivity.this,
							LogCabinMainActivity.class);
					startActivity(intent);
					PassWordActivity.this.finish();
				}

				else {
					Toast.makeText(PassWordActivity.this, "«Î ‰»Î√‹¬Î£°£°£°", Toast.LENGTH_SHORT)
					.show();
					Intent intent = new Intent(PassWordActivity.this,
							PassWordActivity.class);
					startActivity(intent);
					PassWordActivity.this.finish();
				}
			}
		});
		
	}
}
