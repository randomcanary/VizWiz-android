package com.VizWiz.TestRelUI;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Window;

public class CheckResponse extends Activity implements Runnable
{
	TextToSpeech titus;
	static final int CHECKCODE = 5678; //checksum, can be anything
	boolean TTS_ENGINE_IS_READY = false;
	Vibrator v; 
	String queryID = "";
	String resp_time = "NULL"; // the response time from result after extracting
	String resp_resp = "NULL"; //ditto for answer
	String semirawresult = "";
	String sayStuff = "";
	boolean startChecking = false; //not currently used - perhaps delete
	int chkCtr = 0;
	int num_resp = 0;
	int ctr = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i("Preet", "entered onCreate() : Step: " + ++ctr );
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    
	    Intent cIntent = new Intent();
		cIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
		startActivityForResult(cIntent, CHECKCODE);
		
		//while (titus.equals(null))
		//{
			
		//}
		//first, speak "request sent. Awaiting responses"
		
		//retrieving queryID from sendmenu
		Intent intent = getIntent();
		queryID = intent.getStringExtra("queryID");
		//titus stuff
		
		//rtns a resultCode (for the TTS engine check) which is passed into OnActivityResult
		try {
			cIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
			startActivityForResult(cIntent, CHECKCODE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//first, speak "request sent. Awaiting responses"
		
		// start checking for responses
		checkForResponses();
		
		
		
		
	}

	private void checkForResponses() 
	{
		Log.i("Preet", "entered checkForResponses() : Step: " + ++ctr );
		//checks for responses within a time window and outputs responses as and when recd
		String resultt = "NULL"; //this is the raw (JSON File) result from the httpGet. 
		try {
			Thread.sleep(45000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resultt = VWLaunchHTTP(queryID);
		Log.i("Preet","resultt = VWLaunchHTTP(queryID) gives us: " + resultt );
		sayStuff  = readJSONData(resultt, "responses", "text");
		new Thread(this).start();
			
	}

	@Override
	public void run() {

		//isRunning = true;
		//pollButton.setText("Executing...");
		
		
			//Log.i("Preet", "value of i is :"  + "out of numloop: " + numLoop);
			//speakMenuItem(testString);
			try {
				titus.speak(sayStuff, TextToSpeech.QUEUE_FLUSH, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//startChecking = true;
			//checkForResponses();
		
		
		// TODO Auto-generated method stub
		/*runOnUiThread(new Runnable() {
		     public void run() {

		//stuff that updates ui
		 startButton.setText("Start");

		    }
		});*/
		
	
		
	}
	private OnInitListener ttsInitListener = new OnInitListener() 
	{
		public void onInit(int status) 
			{
			Log.i("Preet", "entered OnInitListener() : Step: " + ++ctr );
				if (status == TextToSpeech.SUCCESS) 
				{
		            int result = titus.setLanguage(Locale.US);
		            if (result == TextToSpeech.LANG_MISSING_DATA ||
		                    result == TextToSpeech.LANG_NOT_SUPPORTED) 
		            {
		                Log.e("Preet", "US language not supported");
		            }
		            else
		            {
		            	Log.i("Preet","Initialized TextToSpeech");
		            	/*if (txtView.selected_mItem)
		                {
		            		Log.i("Preet", "txtView.sector is: " + txtView.sector);
		                	speakMenuItem(txtView.sector);
		                }*/
		            }
		            
				}
				else
				{
		            Log.e("Preet","Could not initialize TextToSpeech");
		        }
				
			} 
	};
	 	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.i("Preet", "entered onActivityResult() : Step: " + ++ctr );
		if (requestCode == CHECKCODE) //this is the answer to that specific intent-request you passed
		{
			Log.i("Preet", "requestCode == CHECKCODE");
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) //TTS engine is installed and works
			{
				Log.i("Preet", "resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS");
				titus = new TextToSpeech(this, ttsInitListener);
				TTS_ENGINE_IS_READY = true;
				sayStuff = "request sent. Awaiting responses";
				new Thread(this).start();  
				
			}
			else
	        {
				Log.i("Preet", "resultCode <> TextToSpeech.Engine.CHECK_VOICE_DATA_PASS");
	      // missing resource files for TTS egine operation - create Intent & send Intent-request to install these.
	            Intent installIntent = new Intent();
	            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
		}
	}

	@Override
	protected void onDestroy() 
	{ Log.i("Preet", "entered onDestroy() : Step: " + ++ctr );
		if (titus != null) //if it's still alive, shut it down (you don't put this you'll get a NullPtrEx)
			{
				titus.stop();
				titus.shutdown();
			}
		super.onDestroy();
	}

	public String VWLaunchHTTP(String s) {
		Log.i("Preet", "entered VWLaunchHTTP() : Step: " + ++ctr );
    	String result = "NULL";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet();
        try {
        	httpget.setURI(new URI("https://vizwiz-social.appspot.com/responsecheck" + "?queryId=" + s ));
        }
        catch (URISyntaxException e1) {
        	Log.i("Preet", "Exception mesg: " + e1.getMessage());
        }
        HttpResponse response;
        try {
			response = httpclient.execute(httpget);
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = "";

            line = reader.readLine();
            in.close();
            reader.close();
            result = line;	
            Log.i("Preet",result);
            
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();    
		return result; 
    }        
	public String readJSONData(String s, String keyword, String subkeyword) 
    {
		Log.i("Preet", "entered readJSONData              () : Step: " + ++ctr );
		String ans = ""; 
    	JSONObject json = new JSONObject();
    	try {
			json=new JSONObject(s);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	JSONArray jay;
		try 
		{
			jay = json.getJSONArray(keyword);
			JSONObject jayjay = new JSONObject();
	    	for (int i = 0; i < jay.length(); i++)
	    	{
	    		jayjay = jay.getJSONObject(i);
	    		String ess = jayjay.get(subkeyword).toString();
	    		ans.concat("Answer received: " + ess + ". ");
	    	}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
    	return ans;
    	
    }	
	

}
