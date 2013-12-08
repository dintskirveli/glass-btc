/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.di2356.glass.btcprice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class PriceService extends Service {

	private static final String TAG = "PriceService";
	private static final String LIVE_CARD_ID = "btcprice";

	private PriceSurfaceDrawer mCallback;

	private TimelineManager mTimelineManager;
	private LiveCard mLiveCard;
	private TextToSpeech mSpeech;
	
	public static PriceService mService;

	@Override
	public void onCreate() {
		super.onCreate();
		mTimelineManager = TimelineManager.from(this);
		
		mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // Do nothing.
            }
        });
		mService = this;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mLiveCard == null) {

			Log.d(TAG, "Publishing LiveCard");
			mLiveCard = mTimelineManager.getLiveCard(LIVE_CARD_ID);

			// Keep track of the callback to remove it before unpublishing.
			mCallback = new PriceSurfaceDrawer(this);
			
			refresh();
			
			mLiveCard.enableDirectRendering(true).getSurfaceHolder().addCallback(mCallback);
			mLiveCard.setNonSilent(true);

			Intent menuIntent = new Intent(this, MenuActivity.class);
			mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));

			mLiveCard.publish();
			Log.d(TAG, "Done publishing LiveCard");
		} else {
			
			mLiveCard.unpublish();	
			refresh();
			mLiveCard.publish();
		}
		return START_STICKY;
	} 

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			try {
				BufferedReader in = null;
				HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet("https://coinbase.com/api/v1/prices/spot_rate");
	            HttpResponse response = client.execute(request);
	            response.getStatusLine().getStatusCode();
	            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            String l = in.readLine();
	            JSONObject js = new JSONObject(l);
				return js.getString("amount");
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			} catch (JSONException e1) {
				return "Bad JSON";
			}
		}
		@Override
		protected void onPostExecute(String result) {
			mSpeech.speak("One bitcoin is currently $"+result+", according to coinbase", TextToSpeech.QUEUE_FLUSH, null);
			
			Log.d(TAG, result);
			mCallback.draw(result);
		}
	}
	
	public void refresh() {
		new DownloadWebpageTask().execute();
	}


	@Override
	public void onDestroy() {
		if (mLiveCard != null && mLiveCard.isPublished()) {
			Log.d(TAG, "Unpublishing LiveCard");
			if (mCallback != null) {
				mLiveCard.getSurfaceHolder().removeCallback(mCallback);
			}
			mLiveCard.unpublish();
			mLiveCard = null;
			
			mSpeech.shutdown();

	        mSpeech = null;
		}
		super.onDestroy();
	}
}
