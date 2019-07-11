package com.wikitude.samples;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.sdksamples.R;


/**
 * Activity launched when pressing app-icon.
 * It uses very basic ListAdapter for UI representation
 */
public class Forgot extends Activity {

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// ensure to clean cache when it is no longer required
		ArchitectView.deleteRootCacheDirectory(this);
		this.setContentView(R.layout.forgot );
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void onClickSubmit(View view){
		// getting values
		String email = ((EditText)findViewById(R.id.InputEmail)).getText().toString();

		if (email.length() > 0){
			Toast.makeText(Forgot.this,"A mail have been send to you. Follow the instructions to reset your password.",Toast.LENGTH_LONG).show();
			this.finish();

		} else{
			Toast.makeText(Forgot.this,"You must fill the email field.",Toast.LENGTH_LONG).show();
		}

	}

	public void onClickBack(View view){
		this.finish();
	}




}
