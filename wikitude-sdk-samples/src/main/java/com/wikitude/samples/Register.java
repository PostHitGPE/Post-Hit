package com.wikitude.samples;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wikitude.architect.ArchitectView;
import com.wikitude.samples.utils.urllauncher.APIService;
import com.wikitude.samples.utils.urllauncher.ApiHandler;
import com.wikitude.sdksamples.R;

import org.json.JSONObject;


/**
 * Activity launched when pressing app-icon.
 * It uses very basic ListAdapter for UI representation
 */
public class Register extends Activity {
	public RequestQueue queue;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		queue = Volley.newRequestQueue(this);

		// ensure to clean cache when it is no longer required
		ArchitectView.deleteRootCacheDirectory(this);
		this.setContentView(R.layout.register );
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void onClickSubmit(View view){

		// getting values
		String email = ((EditText)findViewById(R.id.InputEmail)).getText().toString();
		String login = ((EditText)findViewById(R.id.InputLogin)).getText().toString();
		String password = ((EditText)findViewById(R.id.InputPassword)).getText().toString();
		String passwordConfirm = ((EditText)findViewById(R.id.InputPasswordConfirm)).getText().toString();

		if (email.length() > 0
				&& login.length() > 0
				&& password.length() > 0
				&& passwordConfirm.length() > 0){
			if (password.length() < 4) Toast.makeText(Register.this,"Your password must contain at least 4 characters..",Toast.LENGTH_LONG).show();
			if (password.equals(passwordConfirm)){
				APIService api =  APIService.getInstance();

				api.userCreate(this, email, login, password);
			}
			else{
				Toast.makeText(Register.this,"Password confirmation does not match.",Toast.LENGTH_LONG).show();
			}
		} else{
			Toast.makeText(Register.this,"You must fill all the fields.",Toast.LENGTH_LONG).show();
		}

	}

	public void onClickBack(View view){
		this.finish();
	}




}
