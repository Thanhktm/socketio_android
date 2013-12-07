package com.example.socketio;

import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.StringCallback;

public class ScreenChat extends Activity {
	EditText mEditMessage;
	Button mBtnSend;
	ListView mListMessage;

	public static final String TAG = "socketIO";
	boolean isConnected;
	public static final String KEY_DEVICE_ID = "sender";
	public static final String KEY_MESSAGE = "message";
	public String device_id;
	SocketIOClient mClient;
	MessageAdapter mMessageAdapter;
	String userName;
	ArrayList<Message> mMessages = new ArrayList<Message>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_chat);
		device_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			userName = extras.getString("UserName");
		}
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mEditMessage = (EditText) findViewById(R.id.txt_message);
		mListMessage = (ListView) findViewById(R.id.list_message);
		mMessageAdapter = new MessageAdapter(this, mMessages);
		mListMessage.setAdapter(mMessageAdapter);
		mMessageAdapter.notifyDataSetChanged();

		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mClient == null)
					return;
				Message message = new Message();
				message.setMessage(mEditMessage.getText().toString());
				message.setSender(userName);
				Gson gson = new Gson();
				String json = gson.toJson(message);
				if (mClient.isConnected()) {
					mClient.emit(json);
					mEditMessage.getText().clear();
					mMessages.add(message);
					mMessageAdapter.notifyDataSetChanged();
				}
			}
		});
		
		
		SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://162.243.151.197:8000", mConnectCallback);
	}

	public void scrollMyListViewToBottom() {
		mListMessage.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				mMessageAdapter.notifyDataSetChanged();
				mListMessage.setSelection(mMessageAdapter.getCount() - 1);
			}
		});
	}

	ConnectCallback mConnectCallback = new ConnectCallback() {

		@Override
		public void onConnectCompleted(Exception ex, SocketIOClient client) {
			if (ex != null) {
				ex.printStackTrace();
				return;
			}

			client.setStringCallback(new StringCallback() {
				@Override
				public void onString(String string, Acknowledge acknowledge) {
					Log.d("SOCKET", string);
				}
			});

			client.setJSONCallback(new JSONCallback() {
				@Override
				public void onJSON(JSONObject jsonObject, Acknowledge acknowledge) {
					Log.d("SOCKET", jsonObject.toString());
				}
			});

			client.on("event", new EventCallback() {
				@Override
				public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
					Log.d("SOCKET", jsonArray.toString());
					Type listType = new TypeToken<ArrayList<Message>>() {
					}.getType();
					Gson gson = new Gson();
					ArrayList<Message> messages = gson.fromJson(jsonArray.toString(), listType);
					mMessages.addAll(messages);
					scrollMyListViewToBottom();
				}
			});
			mClient = client;
		}
	};
}
