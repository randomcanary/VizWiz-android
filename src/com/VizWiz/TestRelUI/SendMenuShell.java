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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.TextView;

public class SendMenuShell extends Activity implements Runnable //runnable to prevent anr
{
SendMenuView txtView;
TryAgain tryView;
TextToSpeech titus;
static final int CHECKCODE = 5678; //checksum, can be anything
boolean TTS_ENGINE_IS_READY = false;
Vibrator v; 
String kweryID = "NULL";
int ctr = 0;
JSONArray jarr = new JSONArray();
int chkCtr = 0;
String ans = " ";
boolean keepChecking = false;
ArrayList<JSONObject> ansarr = new ArrayList<JSONObject>(); 
int prevSize = 0; //this is the prev size of the JSON Array - used when we call for answers repeatedly, 
//and just want to put answers into ansarr which aren't there as yet. 
int ansarr_index = -1;
boolean runonce = true;

@Override
protected void onCreate(Bundle savedInstanceState) 
{
	super.onCreate(savedInstanceState);
	if (getIntent().hasExtra("launchTryAgain"))	
	{
		Intent intent = getIntent();
		kweryID = intent.getStringExtra("queryID");
		
		tryView = new TryAgain(this);
		setContentView(tryView);
	}
	// TODO Auto-generated method stub
	else
	{
		txtView = new SendMenuView(this);
		setContentView(txtView);
	}
	
	v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
	
	//section1 of TTS: TTS engine check
	Intent chkIntent = new Intent();
	//rtns a resultCode (for the TTS engine check) which is passed into OnActivityResult
	chkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
	startActivityForResult(chkIntent, CHECKCODE);
	
}

private OnInitListener ttsInitListener = new OnInitListener() 
{

	public void onInit(int status) 
		{
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
	if (requestCode == CHECKCODE) //this is the answer to that specific intent-request you passed
	{
		if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) //TTS engine is installed and works
		{
			titus = new TextToSpeech(this, ttsInitListener);
			TTS_ENGINE_IS_READY = true;
			
		}
		else
        {
      // missing resource files for TTS egine operation - create Intent & send Intent-request to install these.
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        }
	}
}

	void speakMenuItem(String menuItem)
	{
		Log.i("Preet", "menuItem is : " + menuItem);
		titus.speak(menuItem, TextToSpeech.QUEUE_FLUSH, null);
	}
//Once your app is done with the TTS Engine, pls shut it down so other apps can use it. 
@Override
	protected void onDestroy() 
	{
		if (titus != null) //if it's still alive, shut it down (you don't put this you'll get a NullPtrEx)
		{
			titus.stop();
			titus.shutdown();
		}
	super.onDestroy();
	}
	void chkResponse(String ID)
	{
		//step1: get kweryID val
		kweryID = ID;
		//step2: invalidate view 
		//setContentView(new TextView(this));
		//step3: speak "request sent. Awaiting responses" - moved to run()
		//titus.speak("request sent. Awaiting responses.", TextToSpeech.QUEUE_FLUSH, null);
		//step4: check for responses
		//4a: get the response jsonarray
		new Thread(this).start();
				
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
	public JSONArray getJSONArray(String s, String keyword) 
    {
		Log.i("Preet", "entered getJSONArray() : Step: " + ++ctr );
		String an = ""; 
    	JSONObject json = new JSONObject();
    	try {
			json=new JSONObject(s);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	JSONArray jay = null;
		try 
		{
			jay = json.getJSONArray(keyword);
			/*JSONObject jayjay = new JSONObject();
	    	for (int i = 0; i < jay.length(); i++)
	    	{
	    		jayjay = jay.getJSONObject(i);
	    		String ess = jayjay.get(subkeyword).toString();
	    		ans.concat("Answer received: " + ess + ". ");
	    	}*/
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
    	return jay;
    }

	@Override
	public void run() 
	{
		titus.speak("request sent. Awaiting responses.", TextToSpeech.QUEUE_FLUSH, null);
		/*while (jarr.equals(null))
		{
			String result1 = VWLaunchHTTP(kweryID);
			jarr = getJSONArray(result1, "responses");	
		}*/
		//4b: if jarr is not null and contains at least one response, 
		// proceed with processing it - storing it and then speaking it. 
		if ((jarr.length() < 1) || keepChecking)
		{
			chkCtr++;
			try 
			{
				Thread.sleep(25000);
			} catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//titus.speak("no answers received as yet, trying again.",  TextToSpeech.QUEUE_FLUSH, null);
			String result1 = VWLaunchHTTP(kweryID);
			Log.i("Preet", "result1 is : " + result1.toString());
			jarr = getJSONArray(result1, "responses");	
			//String jarrStr = jarr.toString();
			//jarrStr = jarrStr.substring(1, jarrStr.length()-1);
			//Log.i("Preet", "jarrStr is : " + jarrStr);
			/*try {
				jarr = new JSONArray(jarrStr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			//Log.i("Preet", "jarr (after jarrStr) is : " + jarr.toString());
			Log.i("Preet", "kweryID is : " + kweryID);
		} 
			 String ess = "";
			JSONObject jayjay = new JSONObject();
			Log.i("Preet", "jarr.length is : " + jarr.length());
			if (prevSize == jarr.length()) titus.speak("No new answers so far.",  TextToSpeech.QUEUE_FLUSH, null);
	    	for (int i = prevSize; i < jarr.length(); i++)
	    	{
	    		try {
					jayjay = jarr.getJSONObject(i); //this works fine - we get {name:value,...}
					Log.i("Preet", "jayjay is: " + jayjay.toString());
					ansarr.add(jayjay);
					ess = jayjay.getString("text");
					Log.i("Preet", "The value of i in the loop is: "+ i + ", and ess is:" + ess);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		ans = ans.concat("We have the following answer : " + ess + ". ");
	    	}
	    	prevSize = jarr.length();
	    	
	    	if (ansarr.size() > 0)
	    	{
	    		titus.speak("Answers received, press next answer to hear answers.", TextToSpeech.QUEUE_FLUSH, null);
	    		Log.i("Preet", "checking ansarr.size in the thread: " + ansarr.size());
	    	}
	    	else
	    	{
	    		titus.speak("No responses received. Try again later.", TextToSpeech.QUEUE_FLUSH, null);
	    	}
	    	//4b contd: speak ans here  (we're not storing ans for now)
	    	Log.i("Preet", "ans (to be spoken) is : " + ans);
	    	//Step5: now that we have the answer stored in array, we 
	    	//5a: launch a menu that lets us iterate through it
	    	//5b: check for more responses and 5a. 
	    	//so one combined menu - maybe "previous answer", "next answer" and "check responses", and
	    	//"main menu"  - this combined menu will be TryAgain.java 
	    	//titus.speak(ans,  TextToSpeech.QUEUE_FLUSH, null);
	    	//go to separate screen now
		
		 // try again after a pause
		/*if (runonce) 
		{
			titus.speak("request sent. Check for answers now.", TextToSpeech.QUEUE_FLUSH, null);
			tryView = new TryAgain(this);
			runOnUiThread(new Runnable() {
			    public void run() 
			    {
					setContentView(tryView);			
			    }
			});
			runonce = false;
		}*/
		
	}
	String getTextfromJSONnext() throws JSONException
	{
		if ((ansarr_index + 1) < jarr.length())
		{
			Log.i("Preet", "ansarr_index value is " + ansarr_index);
			Log.i("Preet", "size of ansarr is: " + ansarr.size());
		
			try {
				return ansarr.get(++ansarr_index).getString("text");
			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				return "You are at the end of the answer list.";
			}
		}
		else return "You are at the end of the answer list.";
	}
	String getTextfromJSONprev() throws JSONException
	{
		Log.i("Preet", "ansarr_index value is " + ansarr_index);
		Log.i("Preet", "size of ansarr is: " + ansarr.size());
	
		if ((ansarr_index -1) >= 0)
		{
			try {
				return ansarr.get(--ansarr_index).getString("text");
			} catch (IndexOutOfBoundsException e) {
				// TODO Auto-generated catch block
				return "You are at the beginning of the answer list.";
			}
		}
		else return "You are at the beginning of the answer list.";
	}
	
}
