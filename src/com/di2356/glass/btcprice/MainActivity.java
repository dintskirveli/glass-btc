
package com.di2356.glass.btcprice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity{
	private TextToSpeech mSpeech;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            	final String TAG = "speech";
            	UtteranceProgressListener utteranceProgressListener=new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        Log.d(TAG, "onStart ( utteranceId :"+utteranceId+" ) ");
                    }

                    @Override
                    public void onError(String utteranceId) {
                        Log.d(TAG, "onError ( utteranceId :"+utteranceId+" ) ");
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        Log.d(TAG, "onDone ( utteranceId :"+utteranceId+" ) ");
                        finish();
                    }
                };
                
                mSpeech.setOnUtteranceProgressListener(utteranceProgressListener);

            }
        });
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		setContentView(R.layout.progress);
		new DownloadWebpageTask().execute();
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
			HashMap<String, String> params = new HashMap<String, String>();

			params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"stringId");
			
			mSpeech.speak("One bitcoin is currently $"+result+", according to coinbase", TextToSpeech.QUEUE_FLUSH, params);
			
			Card card = new Card(getBaseContext());
			card.setText("$"+result+" USD");
			card.setInfo("Coinbase");
			card.setTimestamp(new Timestamp(new Date().getTime()).toString());
			View cardView = card.toView();
			setContentView(cardView);
		}
	}
	
	public void refresh() {
		new DownloadWebpageTask().execute();
	}


	@Override
	public void onDestroy() {
			
		mSpeech.shutdown();
	
	    mSpeech = null;
		
		super.onDestroy();
	}

}
