package com.VizWiz.TestRelUI;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class RecAudioShell extends Activity {
RecAudioMenuView txtView;
TextToSpeech titus;
static final int CHECKCODE = 5678; //checksum, can be anything
boolean TTS_ENGINE_IS_READY = false;
Vibrator v ;

@Override
protected void onCreate(Bundle savedInstanceState) 
{
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	txtView = new RecAudioMenuView(this);
	Log.i("Preet", "new RecAudioMenuView created!");
	setContentView(txtView);
	Log.i("Preet", "setContentView set!");
	
	//section1 of TTS: TTS engine check
	Intent chkIntent = new Intent();
	//rtns a resultCode (for the TTS engine check) which is passed into OnActivityResult
	chkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA); 
	startActivityForResult(chkIntent, CHECKCODE);
	v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
	
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

}
