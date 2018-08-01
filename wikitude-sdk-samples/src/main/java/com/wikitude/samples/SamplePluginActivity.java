package com.wikitude.samples;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.CaptureScreenCallback;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;
import com.wikitude.common.camera.CameraSettings;
import com.wikitude.common.plugins.PluginManager;
import com.wikitude.sdksamples.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class SamplePluginActivity extends AbstractArchitectCamActivity {

	private static final String TAG = "SamplePluginActivity";

	/**
	 * last time the calibration toast was shown, this avoids too many toast shown when compass needs calibration
	 */
	private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();

	@Override
	public String getARchitectWorldPath() {
		return getIntent().getExtras().getString(
				MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL);
	}

	@Override
	public String getActivityTitle() {
		return (getIntent().getExtras() != null && getIntent().getExtras().get(
				MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING) != null) ? getIntent()
				.getExtras().getString(MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_TITLE_STRING)
				: "Test-World";
	}

	@Override
	public int getContentViewId() {
		return R.layout.sample_cam;
	}

	@Override
	public int getArchitectViewId() {
		return R.id.architectView;
	}

	@Override
	public String getWikitudeSDKLicenseKey() {
		return WikitudeSDKConstants.WIKITUDE_SDK_KEY;
	}

	@Override
	protected void onPostCreate(final Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		this.architectView.registerNativePlugins("wikitudePlugins", "barcode", new PluginManager.PluginErrorCallback() {
			@Override
			public void onRegisterError(int errorCode, String errorMessage) {
				Log.v(TAG, "Plugin failed to load. Reason: " + errorMessage);
			}
		});
	}

	@Override
	public SensorAccuracyChangeListener getSensorAccuracyListener() {
		return new SensorAccuracyChangeListener() {
			@Override
			public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
				if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && SamplePluginActivity.this != null && !SamplePluginActivity.this.isFinishing() && System.currentTimeMillis() - SamplePluginActivity.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
					Toast.makeText( SamplePluginActivity.this, R.string.compass_accuracy_low, Toast.LENGTH_LONG ).show();
					SamplePluginActivity.this.lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
				}
			}
		};
	}

	@Override
	public ArchitectView.ArchitectWorldLoadedListener getWorldLoadedListener() {
		return new ArchitectView.ArchitectWorldLoadedListener() {
			@Override
			public void worldWasLoaded(String url) {
				Log.i(TAG, "worldWasLoaded: url: " + url);
			}

			@Override
			public void worldLoadFailed(int errorCode, String description, String failingUrl) {
				Log.e(TAG, "worldLoadFailed: url: " + failingUrl + " " + description);
			}
		};
	}


	@Override
	public ArchitectJavaScriptInterfaceListener getArchitectJavaScriptInterfaceListener() {
		return new ArchitectJavaScriptInterfaceListener() {
			@Override
			public void onJSONObjectReceived(JSONObject jsonObject) {
				try {
					switch (jsonObject.getString("action")) {
						case "present_poi_details":
							final Intent poiDetailIntent = new Intent(SamplePluginActivity.this, SamplePoiDetailActivity.class);
							poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_ID, jsonObject.getString("id"));
							poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_TITILE, jsonObject.getString("title"));
							poiDetailIntent.putExtra(SamplePoiDetailActivity.EXTRAS_KEY_POI_DESCR, jsonObject.getString("description"));
							SamplePluginActivity.this.startActivity(poiDetailIntent);
							break;

						case "capture_screen":
							SamplePluginActivity.this.architectView.captureScreen(CaptureScreenCallback.CAPTURE_MODE_CAM_AND_WEBVIEW, new CaptureScreenCallback() {

								@Override
								public void onScreenCaptured(final Bitmap screenCapture) {
									// store screenCapture into external cache directory
									final File screenCaptureFile = new File(Environment.getExternalStorageDirectory().toString(), "screenCapture_" + System.currentTimeMillis() + ".jpg");

									// 1. Save bitmap to file & compress to jpeg. You may use PNG too
									try {
										final FileOutputStream out = new FileOutputStream(screenCaptureFile);
										screenCapture.compress(Bitmap.CompressFormat.JPEG, 90, out);
										out.flush();
										out.close();

										// 2. create send intent
										final Intent share = new Intent(Intent.ACTION_SEND);
										share.setType("image/jpg");
										share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(screenCaptureFile));

										// 3. launch intent-chooser
										final String chooserTitle = "Share Snaphot";
										SamplePluginActivity.this.startActivity(Intent.createChooser(share, chooserTitle));

									} catch (final Exception e) {
										// should not occur when all permissions are set
										SamplePluginActivity.this.runOnUiThread(new Runnable() {

											@Override
											public void run() {
												// show toast message in case something went wrong
												Toast.makeText(SamplePluginActivity.this, "Unexpected error, " + e, Toast.LENGTH_LONG).show();
											}
										});
									}
								}
							});
							break;
					}
				} catch (JSONException e) {
					Log.e(TAG, "onJSONObjectReceived: ", e);
				}
			}
		};
	}

	@Override
	public ILocationProvider getLocationProvider(final LocationListener locationListener) {
		return new LocationProvider(this, locationListener);
	}
	
	@Override
	public float getInitialCullingDistanceMeters() {
		// you need to adjust this in case your POIs are more than 50km away from user here while loading or in JS code (compare 'AR.context.scene.cullingDistance')
		return ArchitectViewHolderInterface.CULLING_DISTANCE_DEFAULT_METERS;
	}

	@Override
	protected boolean hasGeo() {
		return getIntent().getExtras().getBoolean(
				MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_GEO);
	}

	@Override
	protected boolean hasIR() {
		return getIntent().getExtras().getBoolean(
				MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_IR);
	}
	
	@Override
	protected boolean hasInstant() {
		return getIntent().getExtras().getBoolean(
				MainSamplesListActivity.EXTRAS_KEY_ACTIVITY_INSTANT);
	}

	@Override
	protected CameraSettings.CameraPosition getCameraPosition() {
		return CameraSettings.CameraPosition.DEFAULT;
	}

	@Override
	public CameraSettings.CameraResolution getCameraResolution() {
		return CameraSettings.CameraResolution.SD_640x480;
	}
}
