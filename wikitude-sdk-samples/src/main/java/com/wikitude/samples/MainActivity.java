package com.wikitude.samples;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectStartupConfiguration;
import com.wikitude.common.util.SDKBuildInformation;
import com.wikitude.samples.utils.urllauncher.APIService;
import com.wikitude.samples.utils.urllauncher.ApiHandler;
import com.wikitude.sdksamples.R;
import com.wikitude.tools.device.features.MissingDeviceFeatures;

import org.json.JSONException;


/**
 * Activity launched when pressing app-icon.
 * It uses very basic ListAdapter for UI representation
 */
public class MainActivity extends ListActivity{

	public static final String EXTRAS_KEY_ACTIVITY_TITLE_STRING = "activityTitle";
	public static final String EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL = "activityArchitectWorldUrl";

	public static final String EXTRAS_KEY_ACTIVITY_IR = "activityIr";
	public static final String EXTRAS_KEY_ACTIVITY_GEO = "activityGeo";
	public static final String EXTRAS_KEY_ACTIVITY_INSTANT = "activityInstant";
	public static final String EXTRAS_KEY_API_ENDPOINT = "apiEndPoint";
	public String API_ENDPOINT = "";

	private String API_TOKEN = "";
	public RequestQueue queue;


	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		queue = Volley.newRequestQueue(this);
		// ensure to clean cache when it is no longer required
		ArchitectView.deleteRootCacheDirectory(this);
		this.setContentView(R.layout.list_startscreen );
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void onClickForgotPassword(View view){
		final String className = "com.wikitude.samples.Forgot";
		final Intent intent;
		try{
			intent= new Intent(this, Class.forName(className));
			this.startActivityForResult( intent , 200);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onClickRegister(View view){
		final String className = "com.wikitude.samples.Register";
		final Intent intent;
		try{
			intent= new Intent(this, Class.forName(className));
			this.startActivityForResult( intent , 200);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onClickLogin(View view){

		// getting values
		EditText inputId = (EditText)findViewById(R.id.inputId);
		EditText inputPassword = (EditText)findViewById(R.id.InputPassword);
		String login = inputId.getText().toString();
		String password = inputPassword.getText().toString();

		if (login.length() > 0 && password.length() > 0){
			// call route authentification

			APIService api =  APIService.getInstance();
			api.loginCheck(this, login, password);
		}else{
			if (login.length() == 0){ // login empty
				Toast.makeText(MainActivity.this,"Type a login to log in",Toast.LENGTH_LONG).show();
			}
			else if (password.length() == 0){ // password empty
				Toast.makeText(MainActivity.this,"Password cannot be empty.",Toast.LENGTH_LONG).show();
			}
		}
	}

	public void goPostHitActivity(String token){

		API_TOKEN = token;
		final String className = "com.wikitude.samples.AutoHdPostHitActivity";
		final Intent intent;
		try{
			intent= new Intent(this, Class.forName(className));

			intent.putExtra(EXTRAS_KEY_ACTIVITY_TITLE_STRING, "PostHit");
			intent.putExtra(EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL, "samples/10_Launch$app_5_PostHit/index.html");
			intent.putExtra(EXTRAS_KEY_ACTIVITY_IR, false);
			intent.putExtra(EXTRAS_KEY_ACTIVITY_GEO, true);
			intent.putExtra(EXTRAS_KEY_ACTIVITY_INSTANT, false);
			intent.putExtra(API_ENDPOINT, Constants.API_ENDPOINT);
			intent.putExtra(EXTRAS_KEY_API_ENDPOINT, API_TOKEN);
			this.startActivity( intent );
		} catch(Exception e){
			e.printStackTrace();
		}

		System.out.println("Going to posthit activity!");
	}
}
