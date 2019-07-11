package com.wikitude.samples;

import android.os.Bundle;
import android.support.annotation.IdRes;

import com.wikitude.common.camera.CameraSettings;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class AutoHdPostHitActivity extends PostHitActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        API_TOKEN = this.getIntent().getExtras().getString("apiEndPoint");
    }

    @Override
    protected void onPostCreate( final Bundle savedInstanceState ) {
        super.onPostCreate(savedInstanceState);

    }


    @Override
    public CameraSettings.CameraResolution getCameraResolution() {

        return CameraSettings.CameraResolution.AUTO;
    }
}
