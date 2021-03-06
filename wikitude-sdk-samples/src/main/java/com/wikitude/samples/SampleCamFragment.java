package com.wikitude.samples;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.hardware.SensorManager;
import android.location.LocationListener;
import android.util.Log;
import android.widget.Toast;

import com.wikitude.architect.ArchitectJavaScriptInterfaceListener;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.ArchitectView.SensorAccuracyChangeListener;
import com.wikitude.samples.utils.urllauncher.ARchitectUrlLauncherCamActivity;
import com.wikitude.sdksamples.R;

import org.json.JSONObject;


public class SampleCamFragment extends AbstractArchitectCamFragmentV4{

	private static final String TAG = "SampleCamFragment";
	/**
	 * last time the calibration toast was shown, this avoids too many toast shown when compass needs calibration
	 */
	private long lastCalibrationToastShownTimeMillis = System.currentTimeMillis();
	
	@Override
	public String getARchitectWorldPath() {
		try {
			final String decodedUrl = URLDecoder.decode(getActivity().getIntent().getExtras().getString(ARchitectUrlLauncherCamActivity.ARCHITECT_ACTIVITY_EXTRA_KEY_URL), "UTF-8");
			return decodedUrl;
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this.getActivity(), "Unexpected Exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getContentViewId() {
		return R.layout.post_hit;
	}

	@Override
	public int getArchitectViewId() {
		return R.id.architectView;
	}
	
	@Override
	public String getWikitudeSDKLicenseKey() {
		return Constants.WIKITUDE_SDK_KEY;
	}
	

	@Override
	public SensorAccuracyChangeListener getSensorAccuracyListener() {
		return new SensorAccuracyChangeListener() {
			@Override
			public void onCompassAccuracyChanged( int accuracy ) {
				/* UNRELIABLE = 0, LOW = 1, MEDIUM = 2, HIGH = 3 */
				if ( accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM && getActivity() != null && !getActivity().isFinishing()  && System.currentTimeMillis() - SampleCamFragment.this.lastCalibrationToastShownTimeMillis > 5 * 1000) {
					Toast.makeText( getActivity(), R.string.compass_accuracy_low, Toast.LENGTH_LONG ).show();
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

			}
		};
	}

	@Override
	public ILocationProvider getLocationProvider(final LocationListener locationListener) {
		return new LocationProvider(this.getActivity(), locationListener);
	}

}
