package com.wikitude.samples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.wikitude.samples.utils.urllauncher.ApiHandler;
import com.wikitude.sdksamples.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SamplePoiDetailActivity extends Activity {

	public static final String EXTRAS_KEY_POI_ID = "id";
	public static final String EXTRAS_KEY_POI_TITILE = "title";
	public static final String EXTRAS_KEY_POI_DESCR = "description";

	public  String boardId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.sample_poidetail);
		boardId = getIntent().getExtras().getString(EXTRAS_KEY_POI_ID);

	}

	private void createPostHit(final String message){
		new Thread() {
			@Override
			public void run() {
				String jsonData;
				try{
					jsonData = "{\"data\":{\"user\":{\"id\":\"\",\"pseudo\":\"notwak\",\"password\":\"1234\"}, \"display_board\":{\"id\":\"" + boardId + "\"},\"post_hit\":{\"longitude\":\"0\", \"latitude\":\"0\", \"axeXYZ\":\"840,84,84\",\"message\":\"" + message + "\"},\"tags\":[]}}";
					String serverUrl = "https://post-hit-posthit.c9users.io/app/public/api/add/post_hit";
					ApiHandler.sendPost(serverUrl, jsonData);
				}
				catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		}.start();

	}

	public void sendPostHit(View view) {

		EditText editText = (EditText) findViewById(R.id.message);
		String message = editText.getText().toString();
		createPostHit(message);
		finish();
	}

}
