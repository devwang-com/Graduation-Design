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
	// ������� ���ڵ���
	private static final String TAG = "LogCabinMainActivity";
	private static final boolean D = true;

	public boolean AppUpdate = false;// �����߸���app���ܱ�־
	private boolean flag_sync_web = false;// �Զ�ͬ����־

	private static final long OPEN_CLOSE_WINDOW_TIME = 42000;// ���ش�ʱ�� ��42s����
	private static final long SYNC_WEB_TIMES = 20;// �Զ�ͬ���Ĵ���
	private static final long SYNC_WEB_PERIOD = 1000;// �Զ�ͬ�������� 1s

	// �����߳�ִ����� ��һ����־�����߳� 1���� 2�ش� 3�Զ�ͬ��
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
	private Button mbutton_sync_web;// ͬ����Web������
	private Button mbutton_bt_conn, mbutton_bt_unconn;
	private Button mbutton_open_rgbled, mbutton_close_rgbled;
	private Button mbutton_open_windows, mbutton_close_windows;
	private Button mbutton_make_heat, mbutton_make_cold;
	private Button mbutton_mode_back_home, mbutton_mode_romantic,
			mbutton_mode_away_home;
	private Button voiceButton;// ����
	// Name of the connected device
	private String mConnectedDeviceName = null;

	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private BluetoothChatService mChatService = null;

	int cout = 0;//���ڼ�����յ���ʪ�ȵĴ���
	long exitTime = 0;//���ڰ������˳�Ӧ��
	// TextView of temp and humd state in room
	private TextView mTimeTextView, mTempTextView, mWindowsStateTextView,
			mRgbLedColorStateTextView;
	private AutoCompleteTextView autoeditTextView;
	// ����ָ�� �༭������ �Զ�ƥ��
	private String[] autoedit_items = { "���ơ�", "�صơ�", "�����", "���ȵ�", "���Ƶ�", "���̵�",
			"������", "���ϵ�", "������", "�ش���" };

	// web
	// Progress Dialog
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	// url to create new product
	private static String url_up = "http://devwang.sinaapp.com/LogCabin/LogCabin_Up.php";// "http://www.devwang.sinaapp.com/test_faceback/up.php";
	private static final String TAG_MESSAGE = "message";

	private RecognizerDialog rd;

	/**
	 * ��ά��
	 */
	private Button qrAppButton, qrWebButton;

	private Vibrator vibrator;// ��

	private EditText appUpdateEditText;

	private String State_Vapour = "��������������";
	private String State_Ir = "û�п������ã�";
	private String State_Sound = "û�����磡";

	private MediaPlayer soundMediaPlayer;// ������Ӧ ��Ƶ

	private LogCabinInfo mLogCabinInfo;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // ��Ļ����
		isFirstInstallApp();
		diyTitle();
		viewInit();

		mTitleLeft.setText(R.string.app_name);// ���� �����ʾ������
		soundMediaPlayer = new MediaPlayer();// ������Ӧ ��Ƶ
		autoeditTextView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, autoedit_items));
		// Ѷ������appid http://open.voicecloud.cn/ ����Լ���appid:54202a57
		rd = new RecognizerDialog(this,
				getString(R.string.str_devwang_voice_key_appid));
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		isSupportBT();
		isNonConnBt();

		mLogCabinInfo = new LogCabinInfo("δ֪", "δ֪", "δ֪", "����", "����", "�޷�",
				"δ֪", "δ֪", "����ָ�", "��û˵����", "����ģʽ");

	}

	@SuppressLint("NewApi")
	@Override
	public void onStart() {// Activity�������ڵ�OnStart()
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {// ��APPͬʱ ����Ƿ��Ѿ������������� ���û������ִ��
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);// ��̨����������
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);// �������󷵻�
			// Otherwise, setup the chat session
		} else {// ����ֻ��Ѿ���������������
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

		// ͬ�����ݵ�Web������ ͬ�����磺��ʪ�ȡ�����״̬���ʵ���ɫ��״̬
		mbutton_sync_web.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (validate()) {
					new Up().execute();
					buttonMusicPlay(R.raw.text_success);
				}
			}
		});

		// ����ͬ���� �Զ���ʱͬ�� �ڶ��γ�����رոù���
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
							// ��ʱ�ķ���
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
					mLogCabinInfo.setACTION_HEAT("����");
					mark = 1;
					buttonMusicPlay(R.raw.tock);
				} else {
					sendBtMessageWithState('x');
					mLogCabinInfo.setACTION_HEAT("ͣ��");
					mark = 0;
					buttonMusicPlay(R.raw.tick);
				}

			}
		});
		mbutton_make_cold.setOnClickListener(new OnClickListener() {
			private int mark = 0;// ��־ ���������뵯��

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mark == 0) {
					sendBtMessageWithState('v');
					mLogCabinInfo.setACTION_COLD("����");
					mark = 1;
					buttonMusicPlay(R.raw.tock);
				} else {
					sendBtMessageWithState('z');
					mLogCabinInfo.setACTION_COLD("ͣ��");
					mark = 0;
					buttonMusicPlay(R.raw.tick);
				}
			}
		});

		mbutton_open_windows.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendBtMessageWithState('m');// ����
			}
		});

		mbutton_close_windows.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendBtMessageWithState('n');// �ش�
			}
		});

		mbutton_bt_conn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mbutton_bt_unconn.setEnabled(true);
				mbutton_bt_conn.setText("ʹ��");
				buttonMusicPlay(R.raw.tock);
				if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
					isConnBt();
					mbutton_bt_conn.setText("����");

					toastDisplay(LogCabinMainActivity.this, "�������豸��"
							+ mConnectedDeviceName, Gravity.CENTER, 0,
							Toast.LENGTH_SHORT);// 0��ʾ ��ͼƬ��ʾ
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
				mbutton_bt_conn.setText("����");
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
				// ������֪������ devwang ���ܴ򿪸��¹���
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
				// ����ָ��
				String str = autoeditTextView.getText().toString().trim();
				mLogCabinInfo.setIDENTIFY_TEXT(str);
				if ("".equals(str)) {
					Toast.makeText(getApplicationContext(), "�� ������������~",
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

				// ���ò���Ƶ�ʣ�Ĭ����16k��android�ֻ�һ��ֻ֧��8k��16k.Ϊ�˸��õ�ʶ��ֱ��Ū��16k���ɡ�
				rd.setSampleRate(RATE.rate16k);

				final StringBuilder sb = new StringBuilder();

				// Log.i(TAG, "ʶ��׼����ʼ.............");

				// ����ʶ���Ļص����
				rd.setListener(new RecognizerDialogListener() {
					@Override
					public void onResults(ArrayList<RecognizerResult> result,
							boolean isLast) {
						for (RecognizerResult recognizerResult : result) {
							sb.append(recognizerResult.text);
							// Log.i(TAG, "ʶ��һ�����Ϊ::"+recognizerResult.text);
						}
					}

					@Override
					public void onEnd(SpeechError error) {
						Log.i("VIOCE_END", "����ʶ�����.............");
						Log.i("VIOCE_END", "====>error:" + error);
						// ˵����� ����ת��Ϊ������Ϣ�� ˳�㷢��
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
						mLogCabinInfo.setHOME_MODE("�ؼ�ģʽ");
						sendBtMessageWithState('c');
						return true;
					}
				});
		mbutton_mode_romantic.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mLogCabinInfo.setHOME_MODE("����ģʽ");
				sendBtMessageWithState('e');
				mRgbLedColorStateTextView.setText("��");
				return true;
			}
		});
		mbutton_mode_away_home
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						mLogCabinInfo.setHOME_MODE("�ؼ�ģʽ");
						
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
		// �ͷ��� ������������������
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
		 * ������Ӧ��Ƶ �ڴ˴� Ҫ�ͷ���Դ
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
	private void sendMessage(String message) {// ͨ�����������ݣ��ַ������ͳ�ȥ
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {// �ж������Ƿ��������豸
																				// δ������ִ��
			Toast.makeText(this, R.string.not_connected, 500).show();
			// isNonConnBt();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {// �����Ѿ��������豸 �жϴ��˵������Ƿ�Ϊ�� ��Ϊ����
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();// ��Ҫ���͵����ݣ��ַ���ת��Ϊ �ֽ�����
			mChatService.write(send);// ��ת��������ݷ��ͳ�ȥ

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);// ������������ ����

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
	// ���� ��Ϣ
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:// �����Ѿ����� ��ʾ����״̬
					mTitle.setText(R.string.title_connected_to);// ׷����ʾ�����豸��
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

				// V vapour ���� ����
				if (readMessage.charAt(0) == 'V') {
					isVapour();
				}

				// I Ir ����
				if (readMessage.charAt(0) == 'I') {
					isIr();
				}
				// S Sound ����
				if (readMessage.charAt(0) == 'S') {
					isSound();
				}

				// T ��ʪ��
				if (readMessage.charAt(0) == 'T') {
					cout++;// ���յ�T�Ĵ���
				} else {
					// T19-70
					mTimeTextView.setText(currentHour + ":" + currentMinute);// ��ʾʵʱ��ʪ��ʱ��
					mTempTextView.setText(readMessage);
					try {
						// �ַ����и�
						String spStr[] = mTempTextView.getText().toString()
								.split("'");
						String spStr1[] = mTempTextView.getText().toString()
								.split(" ");
						mTempTextView.setText(spStr[0] + "��C " + spStr1[1]);
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

	// �˵�����
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
		case R.id.secure_connect_scan:// ��ȫ����ɨ��
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
			return true;
		case R.id.insecure_connect_scan:// ����ȫ����ɨ��
			// Launch the DeviceListActivity to see devices and do scan
			serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent,
					REQUEST_CONNECT_DEVICE_INSECURE);
			return true;
		case R.id.app_developer:
			toastDisplay(
					LogCabinMainActivity.this,"������:��  � \n\nQQ:1120341494\n΢��:dongleixiaxue314\n�� ҳ:http://www.devwang.com",
					Gravity.CENTER, R.drawable.hutview, Toast.LENGTH_SHORT);
			return true;
		case R.id.app_cmd_what:
		case R.id.app_voice_what:
			toast.setText("�������е�һ�������ؼ���\n������������Ӧ����ȷ������\n���硰����ơ�������ɫ������С��ñ����\n�������������ش���\n�ȵ�...");
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
			DialogUtil.showDialog(this, "���ܼҾӲ���δ�仯~", false);
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
			Log.i("Up", "����ͬ��!");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			// pDialog.show();//����ʾ
		}

		/**
		 * Creating product
		 * */
		// ͬ�����ݵ�Web������ ͬ�����磺��ʪ�ȡ�����״̬���ʵ���ɫ��״̬
		// mTempTextView, mWindowsStateTextView,mRgbLedColorStateTextView
		protected String doInBackground(String... args) {

			mLogCabinInfo.setROOM_IN_REALTIME_TEMP_HUMD(mTempTextView.getText()
					.toString());
			mLogCabinInfo
					.setCURRENT_RGB_LED_COLOR_STATE(mRgbLedColorStateTextView
							.getText().toString());
			mLogCabinInfo.setCURRENT_WINDOW_STATE(mWindowsStateTextView
					.getText().toString());

			// mLogCabinInfo.setSTATE_VAPOUR("����");
			// mLogCabinInfo.setSTATE_IR("����");
			// mLogCabinInfo.setSTATE_SOUND("�޷�");
			// mLogCabinInfo.setACTION_HEAT("����");
			// mLogCabinInfo.setACTION_COLD("����");
			// mLogCabinInfo.setIDENTIFY_TEXT("����ָ�");
			// mLogCabinInfo.setIDENTITY_VIOCE("��û˵����");
			// mLogCabinInfo.setHOME_MODE("����ģʽ");

			String realtimetemphumd = mLogCabinInfo
					.getROOM_IN_REALTIME_TEMP_HUMD(); // ������ʪ��״̬
			String windowstate = mLogCabinInfo.getCURRENT_WINDOW_STATE();// ��ǰ������״̬
			String ledcolorstate = mLogCabinInfo
					.getCURRENT_RGB_LED_COLOR_STATE();// ��ǰ����ɫ��״̬

			String state_vapour = mLogCabinInfo.getSTATE_VAPOUR();// ��� ��������
			String state_ir = mLogCabinInfo.getSTATE_IR();// ��� ���⴫����
			String state_sound = mLogCabinInfo.getSTATE_SOUND();// ��� ����������

			String action_heat = mLogCabinInfo.getACTION_HEAT();// ����״̬
			String action_cold = mLogCabinInfo.getACTION_COLD();// ����״̬

			String identify_text = mLogCabinInfo.getIDENTIFY_TEXT();// ����ʶ��
			String identify_vioce = mLogCabinInfo.getIDENTITY_VIOCE();// ����ʶ��

			String home_mode = mLogCabinInfo.getHOME_MODE();// �龰ģʽ

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
			// message Ϊ����doInbackground�ķ���ֵ
			// Toast.makeText(getApplicationContext(), message,
			// Toast.LENGTH_SHORT)
			// .show();
			if (D)
				Log.i("TAG_WEB_MSG", "" + message);

		}

	}

	/**
	 * app���յ� mcu������'V' ��ʾ�� ©��Σ��
	 */
	protected void isVapour() {
		State_Vapour = "��©�������Σ�գ���";
		mLogCabinInfo.setSTATE_VAPOUR(State_Vapour);
		toastDisplay(getApplicationContext(), "�����ˣ�ú��©���ˣ�Σ�գ�����",
				Gravity.CENTER, R.drawable.icon_vapour_white,
				Toast.LENGTH_SHORT);
		vibratorSettings(1);// ��
		State_Vapour = "����";
		mLogCabinInfo.setSTATE_VAPOUR(State_Vapour);
	}

	/**
	 * app���յ� mcu������'I' ��ʾ�� ��������
	 */
	protected void isIr() {
		State_Ir = "���ˣ��п������ã�";
		mLogCabinInfo.setSTATE_IR(State_Ir);
		toastDisplay(getApplicationContext(), "���ˣ��п�������~~~", Gravity.CENTER,
				R.drawable.icon_ir_coming, Toast.LENGTH_SHORT);
		vibratorSettings(1);// ��
		State_Ir = "����";
		mLogCabinInfo.setSTATE_IR(State_Ir);

	}

	/**
	 * app���յ� mcu������'S' ��ʾ�� �������
	 */
	protected void isSound() {
		State_Sound = "���ˣ������ˣ�";
		mLogCabinInfo.setSTATE_IR(State_Ir);
		toastDisplay(getApplicationContext(), "������~~~", Gravity.CENTER,
				R.drawable.icon_sound_wind, Toast.LENGTH_SHORT);
		vibratorSettings(1);// ��
		State_Sound = "�޷�";
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
	 * ������Ӧ��Ƶ ���õ��Ӻ��� ��onDestory��Ҫ�ǵ��ͷ���Դ
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
	 * ʹ��������Ƭ�� ���͵�ָ��Э��
	 * 
	 * @param strCmd
	 */
	private void sendBtMessageWithState(char strCmd) {
		sendMessage(strCmd + "");// char to string
		switch (strCmd) {
		case 'a':

			break;
		case 'b':// b ���� blue

			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_blue));
			mRgbLedColorStateTextView.setTextColor(Color.BLUE);
			break;
		case 'c':

			break;
		case 'd':// d �ص� dieout
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_close));
			mRgbLedColorStateTextView.setTextColor(Color.BLACK);
			buttonMusicPlay(R.raw.tick);
			break;
		case 'e':

			break;
		case 'f':

			break;
		case 'g':// g �̵� green
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

		case 'm':// m ���� motor
			if (D)
				Log.i("TEST_BT_CMD", "===>>����");
			setTempWindowRGBLedStateTextView("", "���ڿ�...", "");
			new Thread(new Runnable() {
				public void run() {
					spandTimeMethod();
					// ��ʱ�ķ���
					handler.sendEmptyMessage(FLAG_HANDLER_MESSAGE_OPEN_WINDOW);
					// ִ�к�ʱ�ķ���֮��������handler
				}
			}).start();

			buttonMusicPlay(R.raw.tock);
			break;
		case 'n':// n �ش�
			if (D)
				Log.i("TEST_BT_CMD", "===>>�ش�");
			setTempWindowRGBLedStateTextView("", "���ڹ�...", "");
			new Thread(new Runnable() {
				public void run() {
					spandTimeMethod();
					// ��ʱ�ķ���
					handler.sendEmptyMessage(FLAG_HANDLER_MESSAGE_CLOSE_WINDOW);
					// ִ�к�ʱ�ķ���֮��������handler
				}
			}).start();

			buttonMusicPlay(R.raw.tick);
			break;
		case 'o':// o �ȵ� orange
			if (D)
				Log.i("TEST_BT_CMD", "===>>�ȵ�");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_orange));
			mRgbLedColorStateTextView.setTextColor(Color.rgb(255, 125, 0));
			break;
		case 'p':// p �ϵ� purple
			if (D)
				Log.i("TEST_BT_CMD", "===>>�ϵ�");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_purple));
			mRgbLedColorStateTextView.setTextColor(Color.rgb(255, 0, 255));
			break;
		case 'q':

			break;

		case 'r':// r ��� red
			if (D)
				Log.i("TEST_BT_CMD", "===>>���");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_red));
			mRgbLedColorStateTextView.setTextColor(Color.RED);
			break;
		case 's':

			break;
		case 't':// t ���� ring-time

			break;

		case 'u':// u ���� ����up
			if (D)
				Log.i("TEST_BT_CMD", "===>>����");
			setTempWindowRGBLedStateTextView("ͣ��", "", "");
			break;
		case 'v':// v ���� ����
			if (D)
				Log.i("TEST_BT_CMD", "===>>����");
			setTempWindowRGBLedStateTextView("ͣ��", "", "");
			break;

		case 'w':// w �׵� ����white
			if (D)
				Log.i("TEST_BT_CMD", "===>>����");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_open));
			mRgbLedColorStateTextView.setTextColor(Color.BLACK);
			buttonMusicPlay(R.raw.tock);
			break;
		case 'x':// x ͣ��
			if (D)
				Log.i("TEST_BT_CMD", "===>>ͣ��");
			setTempWindowRGBLedStateTextView("����", "", "");
			break;
		case 'y':// �Ƶ� yellow
			if (D)
				Log.i("TEST_BT_CMD", "===>>�Ƶ�");
			setTempWindowRGBLedStateTextView("", "",
					getString(R.string.str_yellow));
			mRgbLedColorStateTextView.setTextColor(Color.YELLOW);
			break;
		case 'z':// z ͣ��
			if (D)
				Log.i("TEST_BT_CMD", "===>>����");
			setTempWindowRGBLedStateTextView("����", "", "");
			break;

		default:
			break;
		}
		Log.i("TEST_BT_CMD", strCmd + "");
	}

	/**
	 * ���� ��ʪ�� ����״̬ ��ɫ�� ���ı���ʾ
	 * 
	 * @param TempOp
	 * @param WindowState
	 * @param RGBLedState
	 */
	private void setTempWindowRGBLedStateTextView(String TempOp,
			String WindowState, String RGBLedState) {
		if (!("".equals(TempOp))) {
			if (("����".equals(TempOp)) | ("ͣ��".equals(TempOp)))
				mbutton_make_heat.setText(TempOp);
			if (("����".equals(TempOp)) | ("ͣ��".equals(TempOp)))
				mbutton_make_cold.setText(TempOp);
		}
		if (!("".equals(WindowState)))
			mWindowsStateTextView.setText(WindowState);
		if (!("".equals(RGBLedState)))
			mRgbLedColorStateTextView.setText(RGBLedState);
	}

	/**
	 * �ж��Ƿ��һ�ΰ�װʹ�ø�app ��������
	 */
	private void isFirstInstallApp() {
		// ���ǵ�һ�ΰ�װ���������������־
		SharedPreferences preferences = getSharedPreferences("first_pref",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean("isFirstIn", false);
		editor.commit();
	}

	/**
	 * �Զ��������
	 */
	private void diyTitle() {
		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);// �Զ������
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);
	}

	/**
	 * ��ʼ���ؼ� ��xml�ļ���ϵ����
	 */
	private void viewInit() {
		mbutton_bt_conn = (Button) findViewById(R.id.button_bt_conn);// ���Ӱ�ť
		mbutton_bt_unconn = (Button) findViewById(R.id.button_bt_unconn);// �Ͽ���ť
		mbutton_bt_unconn.setEnabled(false);// �տ�ʼ �Ͽ���ť��ʹ��
		mbutton_open_rgbled = (Button) findViewById(R.id.button_openled);// ���ư�ť
		mbutton_close_rgbled = (Button) findViewById(R.id.button_closeled);// �صư�ť
		qrAppButton = (Button) findViewById(R.id.qrAppButton);// ɨ�������APP��ť
		qrWebButton = (Button) findViewById(R.id.qrWebButton);// ɨ��ͷ���Web��ť
		voiceButton = (Button) findViewById(R.id.voiceButton);
		mbutton_open_windows = (Button) findViewById(R.id.button_open_windows);
		mbutton_close_windows = (Button) findViewById(R.id.button_close_windows);

		mbutton_send_cmd = (Button) findViewById(R.id.btn_msg_send);
		mbutton_sync_web = (Button) findViewById(R.id.btn_sync_web);

		mbutton_make_heat = (Button) findViewById(R.id.btn_heat);// ���Ȱ�ť
		mbutton_make_cold = (Button) findViewById(R.id.btn_cold);// ���䰴ť

		mbutton_mode_back_home = (Button) findViewById(R.id.button_mode_back_home);
		mbutton_mode_romantic = (Button) findViewById(R.id.button_mode_romantic);
		mbutton_mode_away_home = (Button) findViewById(R.id.button_mode_away_home);

		mTitleLeft = (TextView) findViewById(R.id.title_left_text);
		mTitle = (TextView) findViewById(R.id.title_right_text);// ���� �ұߵĿؼ�
		// ��ʾ�豸�Ƿ�������
		mTimeTextView = (TextView) findViewById(R.id.tv_time);
		mTempTextView = (TextView) findViewById(R.id.tv_temp);
		mRgbLedColorStateTextView = (TextView) findViewById(R.id.tv_rgbled_state);
		mWindowsStateTextView = (TextView) findViewById(R.id.tv_windows_state);

		autoeditTextView = (AutoCompleteTextView) findViewById(R.id.auto_tv_msg);

	}

	/**
	 * �Ƿ� ��ʼ�� ��ɫ ��ת ��ť
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
	 * ����appʱ ����ֻ��Ƿ�֧����������
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
	 * ʶ������ָ�������ָ��
	 * 
	 * @param str
	 */
	private void identifyTextCmdAndVioce(String str) {
		// TODO Auto-generated method stub
		identifyTextCmdAndVioceOfRgbLedAndWindow(str);
		identifyTextCmdAndVioceOfTempChange(str);
	}

	/**
	 * ʶ������ָ�������ָ�� ��ɫ�� ����
	 */
	private void identifyTextCmdAndVioceOfRgbLedAndWindow(String str) {
		// TODO Auto-generated method stub
		// ��Ȼ�������
		if (str.indexOf(getString(R.string.str_red)) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "�������족��");
			sendBtMessageWithState('r');
		}
		if (str.indexOf(getString(R.string.str_orange), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "�������ȡ���");
			sendBtMessageWithState('o');
		}

		if (str.indexOf(getString(R.string.str_yellow), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "�������ơ���");
			sendBtMessageWithState('y');
		}
		if (str.indexOf(getString(R.string.str_green), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "�������̡���");
			sendBtMessageWithState('g');
		}
		if (str.indexOf(getString(R.string.str_blue), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "������������");
			sendBtMessageWithState('b');
		}
		if (str.indexOf(getString(R.string.str_purple), 0) != -1) {
			if (D)
				Log.i("TEST_VIOCE", "�������ϡ���");
			sendBtMessageWithState('p');
		}
		// ���� �ص�
		
			if ("���ơ�".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "���������ơ�����");
				sendBtMessageWithState('w');
			}
			if ("�صơ�".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "�������صơ�����");
				sendBtMessageWithState('d');
			}
		
		// ���� �ش�
		
			if ("������".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "����������������");
				sendBtMessageWithState('m');
			}
			if ("�ش���".equals(str)) {
				if (D)
					Log.i("TEST_VIOCE", "�������ش�������");
				sendBtMessageWithState('n');
			}
		
	}

	/**
	 * ����ָ�� ����ָ�� ʶ�� ���� ���� ͣ�� ͣ��
	 * 
	 * @param str
	 */
	private void identifyTextCmdAndVioceOfTempChange(String str) {
		// TODO Auto-generated method stub
		mLogCabinInfo.setHOME_MODE("�Զ���ģʽ");

		if ("���ȡ�".equals(str) || "�е��䡣".equals(str) || "���䰡��".equals(str)
				|| "�����¶ȡ�".equals(str) || "���¡�".equals(str)) {
			sendMessage("u");
			mbutton_make_heat.setText("ͣ��");
		} else if ("���䡣".equals(str) || "�е��ȡ�".equals(str)
				|| "���Ȱ���".equals(str) || "�����¶ȡ�".equals(str)
				|| "���¡�".equals(str)) {
			sendMessage("v");
			mbutton_make_cold.setText("ͣ��");
		} else if ("ͣ�ȡ�".equals(str) || "ֹͣ���ȡ�".equals(str)) {
			sendMessage("x");
			mbutton_make_heat.setText("����");
		} else if ("ͣ�䡣".equals(str) || "ֹͣ���䡣".equals(str)) {
			sendMessage("z");
			mbutton_make_heat.setText("����");
		} else {
			// û����������ָ��
//			if (!("".equals(str))) {
//				Toast.makeText(getApplicationContext(),
//						"�ף�û�� ��" + str + "�� ��������ָ��Ŷ����ʹ����ͨ��~ �����ò˵��鿴����Щ����ָ��~",
//						Toast.LENGTH_SHORT).show();
//			}
		}
	}

	/**
	 * ����δ���� ��ʼ������
	 */
	private void isNonConnBt() {
		setButtonEnabled(false);
		setTextViewInit();
	}

	/**
	 * �����Ѿ����� ��ʼ������
	 */
	public void isConnBt() {
		setButtonEnabled(true);
		setTextViewInit();
	}

	/**
	 * ���������Ƿ����� ��ʼ���ı���ʾ
	 */
	private void setTextViewInit() {
		mRgbLedColorStateTextView.setText(R.string.str_close);
		mRgbLedColorStateTextView.setTextColor(Color.BLACK);
		mWindowsStateTextView.setText(R.string.str_close);
		autoeditTextView.setText("");
		mbutton_bt_conn.setText("����");
	}

	/**
	 * ���������Ƿ����� ��ʹ���벻ʹ�ܰ�ť����ת��ť
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

	// �߳� ������ʾ �����ص�ִ��״̬ �� �Զ�ͬ�����ݵ�web������
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// handler���յ���Ϣ��ͻ�ִ�д˷���
			switch (msg.what) {
			case FLAG_HANDLER_MESSAGE_OPEN_WINDOW:
				// ��ʱʱ�䵽֮��ִ�еĲ���
				setTempWindowRGBLedStateTextView("",
						getString(R.string.str_open), "");
				break;
			case FLAG_HANDLER_MESSAGE_CLOSE_WINDOW:
				// ��ʱʱ�䵽֮��ִ�еĲ���
				setTempWindowRGBLedStateTextView("",
						getString(R.string.str_close), "");
				break;
			case FLAG_HANDLER_MESSAGE_AUTO_SYNC_WEB:// ��ʱͬ�����ݵ�Web������
				if (D)
					Log.i("TEST_Sync", "==>>auto sync to web " + SYNC_WEB_TIMES
							+ " times;" + SYNC_WEB_PERIOD / 1000 + "sִ��һ�Ρ�");
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
	 * ���ش�����... ���ش�ʱ�� ��42s����
	 */
	private void spandTimeMethod() {
		try {
			Thread.sleep(OPEN_CLOSE_WINDOW_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * �ж� ��ɫ��ת��ť ѡ�л������ĸ���ɫ
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
	 * ����ʱ���Զ�ͬ�����ݵ�Web������
	 */
	private void syncTimeMethod() {
		if (flag_sync_web == true) {
			Looper.prepare();// ������һ��ᱨ��
			// ���⣺java.lang.RuntimeException: Can't create handler inside thread
			// that has not called Looper.prepare()
			// ��������߳����淢��message�����̴߳�����handler ����֮ǰ����Looper.prepare()
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
			toastDisplay(LogCabinMainActivity.this, "ͬ�����" + SYNC_WEB_TIMES
					+ "��������ϣ���" + SYNC_WEB_PERIOD + "s��", Gravity.BOTTOM, 0,
					Toast.LENGTH_SHORT);
		}
	}

	private void vibratorSettings(int vibratorTimes) {
		/*
		 * �������𶯴�С����ͨ���ı�pattern���趨���������ʱ��̫�̣���Ч�����ܸо�����
		 */
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		long[] pattern = { 100, 400, 100, 400 }; // ֹͣ ���� ֹͣ ����
		// �ظ����������pattern ���ֻ����һ�Σ�index��Ϊ-1
		vibrator.vibrate(pattern, vibratorTimes);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				vibrator.cancel();
			}
		}, 2000);// �������ȡ��

	}

	private void toastDisplay(Context context, CharSequence text, int gravity,
			int ResImgId, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		if (gravity != 0) {
			switch (gravity) {
			case Gravity.CENTER:
				toast.setGravity(Gravity.CENTER, 0, 0);// ������ʾ x,y�ᶼ��0
				break;
			case Gravity.FILL:
				toast.setGravity(Gravity.FILL, 0, 0);// ������ʾ x,y�ᶼ��0
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
		// ��д ���ؼ� ������ �˳�Ӧ��
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()���ۺ�ʱ���ã��϶�����2000
			{
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
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
