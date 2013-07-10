package com.example.socketio;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.example.socketio.SocketIOClient.Handler;

public class MainActivity extends Activity {
	EditText mEditMessage;
	SocketIOClient client;
	public static final String TAG = "socketIO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mEditMessage = (EditText) findViewById(R.id.txt_message);
		mEditMessage.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode) {

				}
				return true;
			}
		});

		client = new SocketIOClient(URI.create("http://192.168.1.81:8000"), mHandler);
		client.connect();
//		client.emit("OK OK");
	}

	SocketIOClient.Handler mHandler = new Handler() {

		@Override
		public void onConnect() {
			Log.d(TAG, "Connected!");
		}

		@Override
		public void on(String event, JSONArray arguments) {
			Log.d(TAG, String.format("Got event %s: %s", event, arguments.toString()));
		}

		@Override
		public void onJSON(JSONObject json) {
			Log.d(TAG, String.format("Got JSON Object: %s", json.toString()));
		}

		@Override
		public void onMessage(String message) {
			Log.d(TAG, String.format("Got message: %s", message));
		}

		@Override
		public void onDisconnect(int code, String reason) {
			Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
		}

		@Override
		public void onError(Exception error) {
			Log.e(TAG, "Error!", error);
		}

		@Override
		public void onConnectToEndpoint(String endpoint) {
			Log.d(TAG, "Connected to:" + endpoint);

		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
