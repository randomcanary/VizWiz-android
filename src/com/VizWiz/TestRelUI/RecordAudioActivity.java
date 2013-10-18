package com.VizWiz.TestRelUI;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaRecorder;
import android.util.Log;

public class RecordAudioActivity extends Activity 
{
	void initRecordAudio()
    {
    	MediaRecorder recorder = new MediaRecorder();
    	 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	 recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    	 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    	 //recorder.setOutputFile(PATH_NAME);
    	 try
    	 {
    		 recorder.prepare();
    	 }
    	 catch (IOException io)
    	 {
    		 Log.i("Preet", "recorder not initialized properly.");
    	 }
    	 recorder.start();   // Recording is now started
    	
    	 recorder.stop();
    	 recorder.reset();   // You can reuse the object by going back to setAudioSource() step
    	 recorder.release(); // Now the object cannot be reused

    }
    

}
