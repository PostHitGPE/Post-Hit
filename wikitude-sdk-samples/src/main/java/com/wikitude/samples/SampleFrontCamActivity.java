package com.wikitude.samples;


import com.wikitude.common.camera.CameraSettings;

/**
 * This sample will use CameraPosition.FRONT on startup.
 */
public class SampleFrontCamActivity extends SampleCamActivity {

	@Override
	protected CameraSettings.CameraPosition getCameraPosition() {
		return CameraSettings.CameraPosition.FRONT;
	}
}
