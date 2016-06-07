/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devwang.logcabin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devwang.logcabin.browser.LogCabinAppActivity;
import com.devwang.logcabin.browser.LogCabinWebActivity;
import com.devwang.logcabin.bt.BluetoothChatService;
import com.devwang.logcabin.bt.DeviceListActivity;
import com.devwang.logcabin.circlergb.CircleLayout;
import com.devwang.logcabin.circlergb.CircleLayout.OnItemClickListener;
import com.devwang.logcabin.circlergb.CircleLayout.OnItemSelectedListener;
import com.devwang.logcabin.web.DialogUtil;
import com.devwang.logcabin.web.JSONParser;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

@SuppressLint("HandlerLeak")
public class LogCabinMainActivity extends Activity implements
		OnItemSelectedListener, OnItemClickListener {
	// Debugging
	// 定义变量 用于调试
	private static final String TAG = "LogCabinMainActivity";
	private static final boolean D = true;

	public boolean AppUpdate = false;// 开发者更新app功能标志
	private boolean flag_sync_web = false;// 自动同步标志

	private static final long OPEN_CLOSE_WINDOW_TIME = 42000;// 开关窗时间 各42s左右
	private static final long SYNC_WEB_TIMES = 20;// 自动同步的次数
	private static final long SYNC_WEB_PERIOD = 1000;// 自动同步的周期 1s

	// 各子线程执行完毕 发一个标志给主线程 1开窗 2关窗 3自动同步
	private static final int FLAG_HANDLER_MESSAGE_OPEN_WINDOW = 1;
	private static final int FLAG_HANDLER_MESSAGE_CLOSE_WINDOW = 2;
	private static final int FLAG_HANDLER_MESSAGE_AUTO_SYNC_WEB = 3;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
	private static final int REQUEST_ENABLE_BT = 3;

	// Layout Views
	private TextView mTitle, mTitleLeft;
	private Button mbutton_send_cmd;
	private Button mbutton_sync_web;// 同步到Web服务器
	private Button mbutton_bt_conn, mbutton_bt_unconn;
	private Button mbutton_open_rgbled, mbutton_close_rgbled;
	private Button mbutton_open_windows, mbutton_close_windows;
	private Button mbutton_make_heat, mbutton_make_cold;
	private Button mbutton_mode_back_home, mbutton_mode_romantic,
			mbutton_mode_away_home;
	private Button voiceButton;// 语音
	// Name of the connected device
	private String mConnectedDeviceName = null;

	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	int cout = 0;//用于计算接收到温湿度的次数
	long exitTime = 0;//用于按两下退出应用
	// TextView of temp and humd state in room
	private TextView mTimeTextView, mTempTextView, mWindowsStateTextView,
			mRgbLedColorStateTextView;
	private AutoCompleteTextView autoeditTextView;
	// 文字指令 编辑框输入 自动匹配
	private String[] autoedit_items = { "开灯。", "关灯。", "开红灯", "开橙灯", "开黄灯", "开绿灯",
			"开蓝灯", "开紫灯", "开窗。", "关窗。" };

	// web
	// Progress Dialog
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	// url to create new product
	private static String url_up = "http://devwang.sinaapp.com/LogCabin/LogCabin_Up.php";// "http://www.devwang.sinaapp.com/test_faceback/up.php";
	private static final String TAG_MESSAGE = "message";

	private RecognizerDialog rd;

	/**
	 * 二维码
	 */
	private Button qrAppButton, qrWebButton;

	private Vibrator vibrator;// 震动

	private EditText appUpdateEditText;

	private String State_Vapour = "烟雾汽油正常！";
	private String State_Ir = "没有客人来访！";
	private String State_Sound = "没有起大风！";

	private MediaPlayer soundMediaPlayer;// 按键响应 音频

	private LogCabinInfo mLogCabinInfo;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 屏幕常亮
		isFirstInstallApp();
		diyTitle();
		viewInit();

		mTitleLeft.setText(R.string.app_name);// 标题 左边显示的内容
		soundMediaPlayer = new MediaPlayer();// 按键响应 音频
		autoeditTextView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoedit_items));
		// 讯飞语音appid http://open.voicecloud.cn/ 王炜自己的appid:54202a57
		rd = new RecognizerDialog(this,
				getString(R.string.str_devwang_voice_key_appid));
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		isSupportBT();
		isNonConnBt();

		mLogCabinInfo = new LogCabinInfo("未知", "未知", "未知", "无气", "无人", "无风",
				"未知", "未知", "文字指令。", "您没说话！", "浪漫模式");

	}

	@SuppressLint("NewApi")
	@Override
	public void onStart() {// Activity生命周期的OnStart()
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {// 打开APP同时 检测是否已经开启蓝牙功能 如果没开启则执行
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);// 后台打开蓝牙功能
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);// 打开蓝牙后返回
			// Otherwise, setup the chat session
		} else {// 如果手机已经开启了蓝牙功能
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e("TAG_BT", "+ ON RESUME +");
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	@SuppressLint("NewApi")
	private void setupChat() {
		if (D)
			Log.d("TAG_BT", "setupChat()");
		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new BluetoothChatService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");

		// 同步数据到Web服务器 同步内如：温湿度、窗户状态、彩灯颜色和状态
		mbutton_sync_web.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validate()) {
					new Up().execute();
					buttonMusicPlay(R.raw.text_success);
				}
			}
		});

		// 长按同步键 自动定时同步 第二次长安则关闭该功能
		mbutton_sync_web.setOnLongClickListener(new OnLongClickListener() {
			private int mark = 0;

			@Override
			public boolean onLongClick(View v) {
				vibratorSettings(2);
				if (!flag_sync_web)
					mark = 0;
				if (mark == 0) {
					mark = 1;
					flag_sync_web = true;
					if (D)
						Log.i("TEST_Sync", "==>>Button sync yes");
					Toast.makeText(LogCabinMainActivity.this,
							R.string.str_have_start_sync_data_upto_web,
							Toast.LENGTH_SHORT).show();
					new Thread(new Runnable() {
						public void run() {
							// 耗时的方法
							if (D)
								Log.i("TEST_Sync",
										"==>>run func syncTimeMethod()");
							syncTimeMethod();
						}
					}).start();
				} else {
					flag_sync_web = false;
					Toast.makeText(LogCabinMainActivity.this,
							R.string.str_hava_stop_sync_data_upto_web,
							Toast.LENGTH_SHORT).show();
					mark = 0;
					if (D)
						Log.i("TEST_Sync", "==>>Button sync no");
				}

				return true;
			}

		});

		mbutton_make_heat.setOnClickListener(new OnClickListener() {
			private int mark = 0;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mark == 0) {
					sendBtMessageWithState('u');
					mLogCabinInfo.setACTION_HEAT("加热");
					mark = 1;
					buttonMusicPlay(R.raw.tock);
				} else {
					sendBtMessageWithState('x');
					mLogCabinInfo.setACTION_HEAT("停热");
					mark = 0;
					buttonMusicPlay(R.raw.tick);
				}

			}
		});
		mbutton_make_cold.setOnClickListener(new OnClickListener() {
			private int mark = 0;// 标志 按键按下与弹起

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mark == 0) {
					sendBtMessageWithState('v');
					mLogCabinInfo.setACTION_COLD("制冷");
					mark = 1;
					buttonMusicPlay(R.raw.tock);
				} else {
					sendBtMessageWithState('z');
					mLogCabinInfo.setACTION_COLD("停冷");
					mark = 0;
					buttonMusicPlay(R.raw.tick);
				}
			}
		});

		mbutton_open_windows.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendBtMessageWithState('m');// 开窗
			}
		});

		mbutton_close_windows.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendBtMessageWithState('n');// 关窗
			}
		});

		mbutton_bt_conn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mbutton_bt_unconn.setEnabled(true);
				mbutton_bt_conn.setText("使能");
				buttonMusicPlay(R.raw.tock);
				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					isConnBt();
					mbutton_bt_conn.setText("连接");

					toastDisplay(LogCabinMainActivity.this, "已链接设备："
							+ mConnectedDeviceName, Gravity.CENTER, 0,
							Toast.LENGTH_SHORT);// 0表示 无图片显示
				} else {
					Intent serverIntent = new Intent(LogCabinMainActivity.this,
							DeviceListActivity.class);
					startActivityForResult(serverIntent,
							REQUEST_CONNECT_DEVICE_SECURE);

				}

			}
		});
		mbutton_bt_unconn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mChatService != null)
					mChatService.stop();
				mbutton_bt_conn.setEnabled(true);
				mbutton_bt_unconn.setEnabled(false);
				mbutton_bt_conn.setText("连接");
				buttonMusicPlay(R.raw.tick);
				isNonConnBt();
			}
		});

		mbutton_open_rgbled.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendBtMessageWithState('w');
			}
		});
		mbutton_close_rgbled.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendBtMessageWithState('d');
			}
		});

		qrAppButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				toastDisplay(getApplicationContext(),
						getString(R.string.str_open_scan_down_app),
						Gravity.CENTER, R.drawable.logcabin_app_qr,
						Toast.LENGTH_LONG);

			}
		});
		qrAppButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// 开发者知道密码 devwang 才能打开更新功能
				// AppUpdate=true;
				if (AppUpdate) {
					vibratorSettings(1);
					Intent intent = new Intent(LogCabinMainActivity.this,
							LogCabinAppActivity.class);
					startActivity(intent);
				} else {
					toastDisplay(
							getApplicationContext(),
							getString(R.string.str_user_has_not_the_permissions),
							0, 0, Toast.LENGTH_SHORT);
				}
				return true;
			}

		});

		qrWebButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toastDisplay(getApplicationContext(),
						getString(R.string.str_open_scan_access_web),
						Gravity.CENTER, R.drawable.logcabin_web_qr,
						Toast.LENGTH_LONG);
			}
		});
		qrWebButton.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				vibratorSettings(1);
				Intent intent = new Intent(LogCabinMainActivity.this,
						LogCabinWebActivity.class);
				startActivity(intent);
				return true;
			}

		});

		mbutton_send_cmd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 发送指令
				String str = autoeditTextView.getText().toString().trim();
				mLogCabinInfo.setIDENTIFY_TEXT(str);
				if ("".equals(str)) {
					Toast.makeText(getApplicationContext(), "亲 ！请输入命令~",
							Toast.LENGTH_SHORT).show();

				} else {
					identifyTextCmdAndVioce(str);
					buttonMusicPlay(R.raw.text_success);
					Log.i("TEST_TEXT_CMD", "cmd:"+str);
				}
			}

		});
		voiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// showReconigizerDialog();
				rd.setEngine("sms", null, null);

				// 设置采样频率，默认是16k，android手机一般只支持8k、16k.为了更好的识别，直接弄成16k即可。
				rd.setSampleRate(RATE.rate16k);

				final StringBuilder sb = new StringBuilder();

				// Log.i(TAG, "识别准备开始.............");

				// 设置识别后的回调结果
				rd.setListener(new RecognizerDialogListener() {
					@Override
					public void onResults(ArrayList<RecognizerResult> result,
							boolean isLast) {
						for (RecognizerResult recognizerResult : result) {
							sb.append(recognizerResult.text);
							// Log.i(TAG, "识别一条结果为::"+recognizerResult.text);
						}
					}

					@Override
					public void onEnd(SpeechError error) {
						Log.i("VIOCE_END", "语音识别完成.............");
						Log.i("VIOCE_END", "====>error:" + error);
						// 说话完毕 并且转化为文字信息后 顺便发送
						autoeditTextView.setText(sb.toString());
						String str = sb.toString();
						mLogCabinInfo.setIDENTITY_VIOCE(str);
						if ("".equals(sb.toString())) {
							Toast.makeText(
									getApplicationContext(),
									R.string.str_oh_my_dear_you_has_not_say_or_unconn_internet,
									Toast.LENGTH_SHORT).show();
						} else {
							identifyTextCmdAndVioce(str);
						}
					}
				});
				rd.show();
			}
		});

		mbutton_mode_back_home
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						mLogCabinInfo.setHOME_MODE("回家模式");
						sendBtMessageWithState('c');
						return true;
					}
				});
		mbutton_mode_romantic.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mLogCabinInfo.setHOME_MODE("浪漫模式");
				sendBtMessageWithState('e');
				mRgbLedColorStateTextView.setText("闪");
				return true;
			}
		});
		mbutton_mode_away_home
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						mLogCabinInfo.setHOME_MODE("回家模式");
						
						sendBtMessageWithState('c');
						return true;
					}
				});

	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");

	}

	@Override
	public void onStop() {
		super.onStop();
		// 释放震动 用于烟雾蒸汽传感器
		if (vibrator != null)
			vibrator.cancel();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mChatService != null)
			mChatService.stop();
		/**
		 * 按键响应音频 在此处 要释放资源
		 */
		if (soundMediaPlayer != null) {
			soundMediaPlayer.release();
			soundMediaPlayer = null;
		}
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	@SuppressLint("NewApi")
	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {// 通过蓝牙将数据（字符）发送出去
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {// 判断蓝牙是否连接了设备
																				// 未连接则执行
			Toast.makeText(this, R.string.not_connected, 500).show();
			// isNonConnBt();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {// 蓝牙已经连接了设备 判断传人的数据是否为空 不为空则
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();// 将要发送的数据（字符）转换为 字节数组
			mChatService.write(send);// 将转换后的数据发送出去

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);// 发送完数据则 清零

		}
	}

	// The action listener for the EditText widget, to listen for the return key
	@SuppressLint("NewApi")
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
		public boolean onEditorAction(TextView view, int actionId,
				KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message
			if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	// The Handler that gets information back from the BluetoothChatService
	// 处理 信息
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:// 蓝牙已经连接 显示连接状态
					mTitle.setText(R.string.title_connected_to);// 追加显示连接设备名
					mTitle.append(mConnectedDeviceName);
					break;
				case BluetoothChatService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				long time;
				Calendar now = Calendar.getInstance();
				TimeZone timeZone = now.getTimeZone();

				long totalMilliseconds = System.currentTimeMillis()
						+ timeZone.getRawOffset();
				long totalSeconds = totalMilliseconds / 1000;
				int currentSecond = (int) (totalSeconds % 60);
				long totalMinutes = totalSeconds / 60;
				int currentMinute = (int) (totalMinutes % 60);
				long totalHours = totalMinutes / 60;
				int currentHour = (int) (totalHours % 24);
				int totalDays = (int) (totalHours / 24);

				int goDays = 0;
				int surplusDays = 0;
				int goYears = 0;
				int leapyear = 0;

				for (int i = 1970; goDays < totalDays; i++) {
					if (i % 400 == 0 || (i % 4 == 0 && i % 100 != 0)) {
						goDays = goDays + 366;
						leapyear = 1;
					} else {
						goDays = goDays + 365;
						leapyear = 0;
					}
					goYears++;
				}
				String readMessage = new String(readBuf, 0, msg.arg1);

				// V vapour 烟雾 蒸汽
				if (readMessage.charAt(0) == 'V') {
					isVapour();
				}

				// I Ir 红外
				if (readMessage.charAt(0) == 'I') {
					isIr();
				}
				// S Sound 声音
				if (readMessage.charAt(0) == 'S') {
					isSound();
				}

				// T 温湿度
				if (readMessage.charAt(0) == 'T') {
					cout++;// 接收到T的次数
				} else {
					// T19-70
					mTimeTextView.setText(currentHour + ":" + currentMinute);// 显示实时温湿度时间
					mTempTextView.setText(readMessage);
					try {
						// 字符串切割
						String spStr[] = mTempTextView.getText().toString()
								.split("'");
						String spStr1[] = mTempTextView.getText().toString()
								.split(" ");
						mTempTextView.setText(spStr[0] + "°C " + spStr1[1]);
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}

	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	// 菜单设置
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent = null;
		Toast toast = Toast.makeText(LogCabinMainActivity.this, "",
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		switch (item.getItemId()) {
		case R.id.secure_connect_scan:// 安全连接扫描
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:// 不安全连接扫描
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.app_developer:
			toastDisplay(
					LogCabinMainActivity.this,"开发者:王  炜 \n\nQQ:1120341494\n微信:dongleixiaxue314\n主 页:http://www.devwang.com",
					Gravity.CENTER, R.drawable.hutview, Toast.LENGTH_SHORT);
			return true;
		case R.id.app_cmd_what:
		case R.id.app_voice_what:
			toast.setText("包含其中的一个或多个关键字\n即可以做出相应的正确操作，\n诸如“开红灯”、“红色”、“小红帽”、\n“开窗”、“关窗”\n等等...");
			toast.show();
			return true;
		case R.id.app_college:
			toast.setText(R.string.str_college);
			toast.show();
			return true;
		case R.id.app_web_url:
			toastDisplay(
					LogCabinMainActivity.this,
					getString(R.string.http_devwang_sinaapp_com_logcabin_logcabin_web_php),
					Gravity.CENTER, 0, Toast.LENGTH_SHORT);
			return true;
		case R.id.app_update:

			android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(
					LogCabinMainActivity.this);
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.dialogview_appupdate, null);
			dialog.setView(layout);
			appUpdateEditText = (EditText) layout
					.findViewById(R.id.et_appupdate);
			dialog.setPositiveButton(R.string.str_app_update_sure,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String appUpdateEditTextString = appUpdateEditText
									.getText().toString();
							if ("devwang".equals(appUpdateEditTextString
									.toString())) {
								AppUpdate = true;
								Toast.makeText(getApplicationContext(),
										R.string.str_app_update_pass_toast,
										Toast.LENGTH_SHORT).show();
							}
						}
					});
			dialog.show();
			return true;
		}
		return false;
	}

	/**
	 * RGB circle menu
	 */
	@Override
	public void onItemSelected(View view, int position, long id, String name) {
		circleColorSend(position);
	}

	/**
	 * RGB circle menu
	 */
	@Override
	public void onItemClick(View view, int position, long id, String name) {
		circleColorSend(position);
	}

	// web
	private boolean validate() {
		String description = mRgbLedColorStateTextView.getText().toString()
				.trim();
		if (description.equals("")) {
			DialogUtil.showDialog(this, "智能家居参数未变化~", false);
			return false;
		}

		return true;
	}

	/**
	 * Background Async Task to Create new product
	 * */
	class Up extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LogCabinMainActivity.this);
			pDialog.setMessage(getString(R.string.str_sync_ing));
			Log.i("Up", "正在同步!");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			// pDialog.show();//不显示
		}

		/**
		 * Creating product
		 * */
		// 同步数据到Web服务器 同步内如：温湿度、窗户状态、彩灯颜色和状态
		// mTempTextView, mWindowsStateTextView,mRgbLedColorStateTextView
		protected String doInBackground(String... args) {

			mLogCabinInfo.setROOM_IN_REALTIME_TEMP_HUMD(mTempTextView.getText()
					.toString());
			mLogCabinInfo
					.setCURRENT_RGB_LED_COLOR_STATE(mRgbLedColorStateTextView
							.getText().toString());
			mLogCabinInfo.setCURRENT_WINDOW_STATE(mWindowsStateTextView
					.getText().toString());

			// mLogCabinInfo.setSTATE_VAPOUR("无气");
			// mLogCabinInfo.setSTATE_IR("无人");
			// mLogCabinInfo.setSTATE_SOUND("无风");
			// mLogCabinInfo.setACTION_HEAT("加热");
			// mLogCabinInfo.setACTION_COLD("制冷");
			// mLogCabinInfo.setIDENTIFY_TEXT("文字指令。");
			// mLogCabinInfo.setIDENTITY_VIOCE("您没说话！");
			// mLogCabinInfo.setHOME_MODE("浪漫模式");

			String realtimetemphumd = mLogCabinInfo
					.getROOM_IN_REALTIME_TEMP_HUMD(); // 室内温湿度状态
			String windowstate = mLogCabinInfo.getCURRENT_WINDOW_STATE();// 当前窗户的状态
			String ledcolorstate = mLogCabinInfo
					.getCURRENT_RGB_LED_COLOR_STATE();// 当前灯颜色的状态

			String state_vapour = mLogCabinInfo.getSTATE_VAPOUR();// 监测 烟雾传感器
			String state_ir = mLogCabinInfo.getSTATE_IR();// 监测 红外传感器
			String state_sound = mLogCabinInfo.getSTATE_SOUND();// 监测 声音传感器

			String action_heat = mLogCabinInfo.getACTION_HEAT();// 加热状态
			String action_cold = mLogCabinInfo.getACTION_COLD();// 制冷状态

			String identify_text = mLogCabinInfo.getIDENTIFY_TEXT();// 文字识别
			String identify_vioce = mLogCabinInfo.getIDENTITY_VIOCE();// 语音识别

			String home_mode = mLogCabinInfo.getHOME_MODE();// 情景模式

			if (D)
				Log.i("Up", realtimetemphumd + windowstate + ledcolorstate
						+ state_vapour + state_ir + state_sound + action_heat
						+ action_cold + identify_text + identify_vioce
						+ home_mode);
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("realtimetemphumd",
					realtimetemphumd));
			params.add(new BasicNameValuePair("windowstate", windowstate));
			params.add(new BasicNameValuePair("ledcolorstate", ledcolorstate));

			params.add(new BasicNameValuePair("state_vapour", state_vapour));
			params.add(new BasicNameValuePair("state_ir", state_ir));
			params.add(new BasicNameValuePair("state_sound", state_sound));
			params.add(new BasicNameValuePair("action_heat", action_heat));
			params.add(new BasicNameValuePair("action_cold", action_cold));
			params.add(new BasicNameValuePair("identify_text", identify_text));
			params.add(new BasicNameValuePair("identify_vioce", identify_vioce));
			params.add(new BasicNameValuePair("home_mode", home_mode));

			// getting JSON Object
			// Note that create product url accepts POST method
			try {
				JSONObject json = jsonParser.makeHttpRequest(url_up, "POST",
						params);
				Log.i("JSON", "" + json);
				String message = json.getString(TAG_MESSAGE);
				Log.i("MESSAGE", "" + message);
				return message;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			// check for success tag

		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String message) {
			pDialog.dismiss();
			// message 为接收doInbackground的返回值
			// Toast.makeText(getApplicationContext(), message,
			// Toast.LENGTH_SHORT)
			// .show();
			if (D)
				Log.i("TAG_WEB_MSG", "" + message);

		}

	}

	/**
	 * app接收到 mcu传来的'V' 表示有 漏气危险
	 */
	protected void isVapour() {
		State_Vapour = "有漏气情况，危险！！";
		mLogCabinInfo.setSTATE_VAPOUR(State_Vapour);
		toastDisplay(getApplicationContext(), "不好了，煤气漏气了，危险！！！",
				Gravity.CENTER, R.drawable.icon_vapour_white,
				Toast.LENGTH_SHORT);
		vibratorSettings(1);// 震动
		State_Vapour = "无气";
		mLogCabinInfo.setSTATE_VAPOUR(State_Vapour);
	}

	/**
	 * app接收到 mcu传来的'I' 表示有 客人来访
	 */
	protected void isIr() {
		State_Ir = "主人，有客人来访！";
		mLogCabinInfo.setSTATE_IR(State_Ir);
		toastDisplay(getApplicationContext(), "主人！有客人来访~~~", Gravity.CENTER,
				R.drawable.icon_ir_coming, Toast.LENGTH_SHORT);
		vibratorSettings(1);// 震动
		State_Ir = "无人";
		mLogCabinInfo.setSTATE_IR(State_Ir);

	}

	/**
	 * app接收到 mcu传来的'S' 表示有 大风起了
	 */
	protected void isSound() {
		State_Sound = "主人，起大风了！";
		mLogCabinInfo.setSTATE_IR(State_Ir);
		toastDisplay(getApplicationContext(), "起大风了~~~", Gravity.CENTER,
				R.drawable.icon_sound_wind, Toast.LENGTH_SHORT);
		vibratorSettings(1);// 震动
		State_Sound = "无风";
		mLogCabinInfo.setSTATE_SOUND(State_Sound);

	}

	@SuppressLint({ "NewApi", "NewApi" })
	private void connectDevice(Intent data, boolean secure) {
		// Get the device MAC address
		String address = data.getExtras().getString(
				DeviceListActivity.EXTRA_DEVICE_ADDRESS);
		// Get the BLuetoothDevice object
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		// Attempt to connect to the device
		mChatService.connect(device, secure);
	}

	/**
	 * 按键响应音频 调用的子函数 在onDestory中要记得释放资源
	 */
	private void buttonMusicPlay(int resource) {
		if (D)
			Log.i("TEST_MUSIC", "music play");
		try {
			soundMediaPlayer.release();
			soundMediaPlayer = MediaPlayer.create(LogCabinMainActivity.this,
					resource);
			// soundMediaPlayer.prepare();
			soundMediaPlayer.start();
			if (D)
				Log.i("TEST_MUSIC", "music playing");
		} catch (Exception e) {
			Toast.makeText(LogCabinMainActivity.this,
					getString(R.string.str_btn_music_error) + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			if (D)
				Log.i("TEST_MUSIC", "===>>music is error:" + e.getMessage());
		}

	}

	/**
	 * 使用蓝牙向单片机 发送的指令协议
	 * 
	 * @param strCmd
	 */
	private void sendBtMessageWithState(char strCmd) {
		sendMessage(strCmd + "");// char to string
		switch (strCmd) {
		case 'a':

			break;
		case 'b':// b 蓝灯 blue

			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_blue));
			mRgbLedColorStateTextView.setTextColor(Color.BLUE);
			break;
		case 'c':

			break;
		case 'd':// d 关灯 dieout
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_close));
			mRgbLedColorStateTextView.setTextColor(Color.BLACK);
			buttonMusicPlay(R.raw.tick);
			break;
		case 'e':

			break;
		case 'f':

			break;
		case 'g':// g 绿灯 green
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_green));
			mRgbLedColorStateTextView.setTextColor(Color.GREEN);
			break;
		case 'h':

			break;

		case 'i':

			break;
		case 'j':

			break;
		case 'k':

			break;
		case 'l':

			break;

		case 'm':// m 开窗 motor
			if (D)
				Log.i("TEST_BT_CMD", "===>>开窗");
			setTempWindowRGBLedStateTextView("", "正在开...", "");
			new Thread(new Runnable() {
				public void run() {
					spandTimeMethod();
					// 耗时的方法
					handler.sendEmptyMessage(FLAG_HANDLER_MESSAGE_OPEN_WINDOW);
					// 执行耗时的方法之后发送消给handler
				}
			}).start();

			buttonMusicPlay(R.raw.tock);
			break;
		case 'n':// n 关窗
			if (D)
				Log.i("TEST_BT_CMD", "===>>关窗");
			setTempWindowRGBLedStateTextView("", "正在关...", "");
			new Thread(new Runnable() {
				public void run() {
					spandTimeMethod();
					// 耗时的方法
					handler.sendEmptyMessage(FLAG_HANDLER_MESSAGE_CLOSE_WINDOW);
					// 执行耗时的方法之后发送消给handler
				}
			}).start();

			buttonMusicPlay(R.raw.tick);
			break;
		case 'o':// o 橙灯 orange
			if (D)
				Log.i("TEST_BT_CMD", "===>>橙灯");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_orange));
			mRgbLedColorStateTextView.setTextColor(Color.rgb(255, 125, 0));
			break;
		case 'p':// p 紫灯 purple
			if (D)
				Log.i("TEST_BT_CMD", "===>>紫灯");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_purple));
			mRgbLedColorStateTextView.setTextColor(Color.rgb(255, 0, 255));
			break;
		case 'q':

			break;

		case 'r':// r 红灯 red
			if (D)
				Log.i("TEST_BT_CMD", "===>>红灯");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_red));
			mRgbLedColorStateTextView.setTextColor(Color.RED);
			break;
		case 's':

			break;
		case 't':// t 铃声 ring-time

			break;

		case 'u':// u 升温 加热up
			if (D)
				Log.i("TEST_BT_CMD", "===>>加热");
			setTempWindowRGBLedStateTextView("停热", "", "");
			break;
		case 'v':// v 降温 制冷
			if (D)
				Log.i("TEST_BT_CMD", "===>>制冷");
			setTempWindowRGBLedStateTextView("停冷", "", "");
			break;

		case 'w':// w 白灯 开灯white
			if (D)
				Log.i("TEST_BT_CMD", "===>>开灯");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_open));
			mRgbLedColorStateTextView.setTextColor(Color.BLACK);
			buttonMusicPlay(R.raw.tock);
			break;
		case 'x':// x 停热
			if (D)
				Log.i("TEST_BT_CMD", "===>>停热");
			setTempWindowRGBLedStateTextView("加热", "", "");
			break;
		case 'y':// 黄灯 yellow
			if (D)
				Log.i("TEST_BT_CMD", "===>>黄灯");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_yellow));
			mRgbLedColorStateTextView.setTextColor(Color.YELLOW);
			break;
		case 'z':// z 停冷
			if (D)
				Log.i("TEST_BT_CMD", "===>>制冷");
			setTempWindowRGBLedStateTextView("制冷", "", "");
			break;

		default:
			break;
		}
		Log.i("TEST_BT_CMD", strCmd + "");
	}

	/**
	 * 设置 温湿度 窗户状态 彩色灯 的文本显示
	 * 
	 * @param TempOp
	 * @param WindowState
	 * @param RGBLedState
	 */
	private void setTempWindowRGBLedStateTextView(String TempOp,
			String WindowState, String RGBLedState) {
		if (!("".equals(TempOp))) {
			if (("加热".equals(TempOp)) | ("停热".equals(TempOp)))
				mbutton_make_heat.setText(TempOp);
			if (("制冷".equals(TempOp)) | ("停冷".equals(TempOp)))
				mbutton_make_cold.setText(TempOp);
		}
		if (!("".equals(WindowState)))
			mWindowsStateTextView.setText(WindowState);
		if (!("".equals(RGBLedState)))
			mRgbLedColorStateTextView.setText(RGBLedState);
	}

	/**
	 * 判断是否第一次安装使用该app 引导界面
	 */
	private void isFirstInstallApp() {
		// 不是第一次安装不进入引导界面标志
		SharedPreferences preferences = getSharedPreferences("first_pref",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

	/**
	 * 自定义标题栏
	 */
	private void diyTitle() {
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// 自定义标题
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
	}

	/**
	 * 初始化控件 与xml文件联系起来
	 */
	private void viewInit() {
		mbutton_bt_conn = (Button) findViewById(R.id.button_bt_conn);// 连接按钮
		mbutton_bt_unconn = (Button) findViewById(R.id.button_bt_unconn);// 断开按钮
		mbutton_bt_unconn.setEnabled(false);// 刚开始 断开按钮不使能
		mbutton_open_rgbled = (Button) findViewById(R.id.button_openled);// 开灯按钮
		mbutton_close_rgbled = (Button) findViewById(R.id.button_closeled);// 关灯按钮
		qrAppButton = (Button) findViewById(R.id.qrAppButton);// 扫描和下载APP按钮
		qrWebButton = (Button) findViewById(R.id.qrWebButton);// 扫描和访问Web按钮
		voiceButton = (Button) findViewById(R.id.voiceButton);
		mbutton_open_windows = (Button) findViewById(R.id.button_open_windows);
		mbutton_close_windows = (Button) findViewById(R.id.button_close_windows);

		mbutton_send_cmd = (Button) findViewById(R.id.btn_msg_send);
		mbutton_sync_web = (Button) findViewById(R.id.btn_sync_web);

		mbutton_make_heat = (Button) findViewById(R.id.btn_heat);// 加热按钮
		mbutton_make_cold = (Button) findViewById(R.id.btn_cold);// 制冷按钮

		mbutton_mode_back_home = (Button) findViewById(R.id.button_mode_back_home);
		mbutton_mode_romantic = (Button) findViewById(R.id.button_mode_romantic);
		mbutton_mode_away_home = (Button) findViewById(R.id.button_mode_away_home);

		mTitleLeft = (TextView) findViewById(R.id.title_left_text);
		mTitle = (TextView) findViewById(R.id.title_right_text);// 标题 右边的控件
		// 显示设备是否连接上
		mTimeTextView = (TextView) findViewById(R.id.tv_time);
		mTempTextView = (TextView) findViewById(R.id.tv_temp);
		mRgbLedColorStateTextView = (TextView) findViewById(R.id.tv_rgbled_state);
		mWindowsStateTextView = (TextView) findViewById(R.id.tv_windows_state);

		autoeditTextView = (AutoCompleteTextView) findViewById(R.id.auto_tv_msg);

	}

	/**
	 * 是否 初始化 彩色 旋转 按钮
	 * 
	 * @param b
	 */
	private void circleMenuInit(boolean b) {
		// RGB circle menu
		CircleLayout circleMenu = (CircleLayout) findViewById(R.id.main_circle_layout);
		if (b) {
			circleMenu.setOnItemSelectedListener(this);
			circleMenu.setOnItemClickListener(this);
		} else {
			circleMenu.setOnItemSelectedListener(null);
			circleMenu.setOnItemClickListener(null);
		}
	}

	/**
	 * 启动app时 检测手机是否支持蓝牙功能
	 */
	private void isSupportBT() {
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
	}

	/**
	 * 识别文字指令和语音指令
	 * 
	 * @param str
	 */
	private void identifyTextCmdAndVioce(String str) {
		// TODO Auto-generated method stub
		identifyTextCmdAndVioceOfRgbLedAndWindow(str);
		identifyTextCmdAndVioceOfTempChange(str);
	}

	/**
	 * 识别文字指令和语音指令 彩色灯 窗户
	 */
	private void identifyTextCmdAndVioceOfRgbLedAndWindow(String str) {
		// TODO Auto-generated method stub
		// 红橙黄绿蓝紫
		if (str.indexOf(getString(R.string.str_red)) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "包含“红”字");
			sendBtMessageWithState('r');
		}
		if (str.indexOf(getString(R.string.str_orange), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "包含“橙”字");
			sendBtMessageWithState('o');
		}

		if (str.indexOf(getString(R.string.str_yellow), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "包含“黄”字");
			sendBtMessageWithState('y');
		}
		if (str.indexOf(getString(R.string.str_green), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "包含“绿”字");
			sendBtMessageWithState('g');
		}
		if (str.indexOf(getString(R.string.str_blue), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "包含“蓝”字");
			sendBtMessageWithState('b');
		}
		if (str.indexOf(getString(R.string.str_purple), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "包含“紫”字");
			sendBtMessageWithState('p');
		}
		// 开灯 关灯
		
			if ("开灯。".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "包含“开灯”两字");
				sendBtMessageWithState('w');
			}
			if ("关灯。".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "包含“关灯”两字");
				sendBtMessageWithState('d');
			}
		
		// 开窗 关窗
		
			if ("开窗。".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "包含“开窗”两字");
				sendBtMessageWithState('m');
			}
			if ("关窗。".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "包含“关窗”两字");
				sendBtMessageWithState('n');
			}
		
	}

	/**
	 * 文字指令 语音指令 识别 加热 制冷 停热 停冷
	 * 
	 * @param str
	 */
	private void identifyTextCmdAndVioceOfTempChange(String str) {
		// TODO Auto-generated method stub
		mLogCabinInfo.setHOME_MODE("自定义模式");

		if ("加热。".equals(str) || "有点冷。".equals(str) || "好冷啊。".equals(str)
				|| "升高温度。".equals(str) || "升温。".equals(str)) {
			sendMessage("u");
			mbutton_make_heat.setText("停热");
		} else if ("制冷。".equals(str) || "有点热。".equals(str)
				|| "好热啊。".equals(str) || "降低温度。".equals(str)
				|| "降温。".equals(str)) {
			sendMessage("v");
			mbutton_make_cold.setText("停冷");
		} else if ("停热。".equals(str) || "停止加热。".equals(str)) {
			sendMessage("x");
			mbutton_make_heat.setText("加热");
		} else if ("停冷。".equals(str) || "停止制冷。".equals(str)) {
			sendMessage("z");
			mbutton_make_heat.setText("制冷");
		} else {
			// 没有这条语音指令
//			if (!("".equals(str))) {
//				Toast.makeText(getApplicationContext(),
//						"亲！没有 “" + str + "” 这条语音指令哦，请使用普通话~ 打开设置菜单查看有哪些语音指令~",
//						Toast.LENGTH_SHORT).show();
//			}
		}
	}

	/**
	 * 蓝牙未连接 初始化界面
	 */
	private void isNonConnBt() {
		setButtonEnabled(false);
		setTextViewInit();
	}

	/**
	 * 蓝牙已经连接 初始化界面
	 */
	public void isConnBt() {
		setButtonEnabled(true);
		setTextViewInit();
	}

	/**
	 * 根据蓝牙是否连接 初始化文本显示
	 */
	private void setTextViewInit() {
		mRgbLedColorStateTextView.setText(R.string.str_close);
		mRgbLedColorStateTextView.setTextColor(Color.BLACK);
		mWindowsStateTextView.setText(R.string.str_close);
		autoeditTextView.setText("");
		mbutton_bt_conn.setText("连接");
	}

	/**
	 * 根据蓝牙是否连接 来使能与不使能按钮和旋转按钮
	 * 
	 * @param b
	 */
	private void setButtonEnabled(boolean b) {
		mbutton_make_heat.setEnabled(b);
		mbutton_make_cold.setEnabled(b);
		mbutton_send_cmd.setEnabled(b);
		mbutton_sync_web.setEnabled(b);
		mbutton_open_windows.setEnabled(b);
		mbutton_close_windows.setEnabled(b);
		mbutton_open_rgbled.setEnabled(b);
		mbutton_close_rgbled.setEnabled(b);
		mbutton_mode_back_home.setEnabled(b);
		mbutton_mode_romantic.setEnabled(b);
		mbutton_mode_away_home.setEnabled(b);
		autoeditTextView.setEnabled(b);
		circleMenuInit(b);
	}

	// 线程 用于显示 窗开关的执行状态 和 自动同步数据到web服务器
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// handler接收到消息后就会执行此方法
			switch (msg.what) {
			case FLAG_HANDLER_MESSAGE_OPEN_WINDOW:
				// 耗时时间到之后执行的操作
				setTempWindowRGBLedStateTextView("",
						getString(R.string.str_open), "");
				break;
			case FLAG_HANDLER_MESSAGE_CLOSE_WINDOW:
				// 耗时时间到之后执行的操作
				setTempWindowRGBLedStateTextView("",
						getString(R.string.str_close), "");
				break;
			case FLAG_HANDLER_MESSAGE_AUTO_SYNC_WEB:// 定时同步数据到Web服务器
				if (D)
					Log.i("TEST_Sync", "==>>auto sync to web " + SYNC_WEB_TIMES
							+ " times;" + SYNC_WEB_PERIOD / 1000 + "s执行一次。");
				if (validate() & flag_sync_web) {
					new Up().execute();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 开关窗动作... 开关窗时间 各42s左右
	 */
	private void spandTimeMethod() {
		try {
			Thread.sleep(OPEN_CLOSE_WINDOW_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断 彩色旋转按钮 选中或按下了哪个颜色
	 * 
	 * @param sel
	 */
	private void circleColorSend(int sel) {
		switch (sel) {
		case 0:
			sendBtMessageWithState('o');
			break;
		case 1:
			sendBtMessageWithState('y');
			break;
		case 2:
			sendBtMessageWithState('g');
			break;
		case 3:
			sendBtMessageWithState('b');
			break;
		case 4:
			sendBtMessageWithState('p');
			break;
		case 5:
			sendBtMessageWithState('r');
			break;
		default:
			break;
		}
	}

	/**
	 * （定时）自动同步数据到Web服务器
	 */
	private void syncTimeMethod() {
		if (flag_sync_web == true) {
			Looper.prepare();// 不加这一句会报错
			// 问题：java.lang.RuntimeException: Can't create handler inside thread
			// that has not called Looper.prepare()
			// 解决：子线程里面发送message给主线程创建的handler 发送之前加上Looper.prepare()
			for (int i = 0; i < SYNC_WEB_TIMES; i++) {
				try {

					handler.sendEmptyMessage(FLAG_HANDLER_MESSAGE_AUTO_SYNC_WEB);
					if (D)
						Log.i("TEST_Sync", "==>>i=" + i + ";");
					Thread.sleep(SYNC_WEB_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			flag_sync_web = false;
			toastDisplay(LogCabinMainActivity.this, "同步最近" + SYNC_WEB_TIMES
					+ "次数据完毕！（" + SYNC_WEB_PERIOD + "s）", Gravity.BOTTOM, 0,
					Toast.LENGTH_SHORT);
		}
	}

	private void vibratorSettings(int vibratorTimes) {
		/*
		 * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
		 */
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400, 100, 400 }; // 停止 开启 停止 开启
		// 重复两次上面的pattern 如果只想震动一次，index设为-1
		vibrator.vibrate(pattern, vibratorTimes);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				vibrator.cancel();
			}
		}, 2000);// 震动两秒后取消

	}

	private void toastDisplay(Context context, CharSequence text, int gravity,
			int ResImgId, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		if (gravity != 0) {
			switch (gravity) {
			case Gravity.CENTER:
				toast.setGravity(Gravity.CENTER, 0, 0);// 居中显示 x,y轴都是0
				break;
			case Gravity.FILL:
				toast.setGravity(Gravity.FILL, 0, 0);// 居中显示 x,y轴都是0
				break;
			case Gravity.BOTTOM:
				toast.setGravity(Gravity.BOTTOM, 0, 0);
				break;
			default:
				break;
			}
		}
		if (ResImgId != 0) {
			LinearLayout toastView = (LinearLayout) toast.getView();
			ImageView imageCodeProject = new ImageView(getApplicationContext());
			imageCodeProject.setImageResource(ResImgId);
			toastView.addView(imageCodeProject, 0);
		}

		toast.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 重写 返回键 按两下 退出应用
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				if (mChatService != null)
					mChatService.stop();
				finish();
				System.exit(0);
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
