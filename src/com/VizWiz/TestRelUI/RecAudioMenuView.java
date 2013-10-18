package com.VizWiz.TestRelUI;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
//import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class RecAudioMenuView extends TextView implements Runnable{
	int touched = 0; //indicates that the screen has been touched, so can draw menu then. 
	float x_d, y_d, x_m, y_m, x_o, y_o; //d indicates x,y for A_Down, m for A_Move, and o, the offset (x_m-x_d)
	boolean selected_mItem = false;
	int ctr_threads = 0;
	//String selected;
	String sector; 
	private RecAudioShell parent;
	Thread gThread;
	MediaRecorder recorder;
	boolean startRec = false;
	

	public RecAudioMenuView(Context context) {
		super(context);
		parent = ((RecAudioShell) context);
		gThread = new Thread(this);
		initStoragePathAndRecordAudio();
		
		//Log.i("Preet", "RecAudioMenuView constructor finished");
		// The Activity RecAudioShell (the equivalent of Marvin/InitMenuShell) must be linked here. 
	}
	
	void initStoragePathAndRecordAudio()
	{
		//FileOutputStream fileOutputStream = null;
        String path;    		
        
		path = Environment.getExternalStorageDirectory().toString() + File.separator + "VizWizFiles";
		File routesRoot = new File(path);	
		
		try {
			if(routesRoot.exists()) {
				Log.i("Preet","Directory exists");
			}
			else {
				if (routesRoot.mkdir()) {
					Log.i("Preet","Directory Created");
				}
				else {
					Log.i("Preet","CANNOT create directory");    					
				}
			}
		}
		catch (Exception e) {
			Log.i("Preet","Exception creating directory");
			e.printStackTrace();
		}
		path = routesRoot.toString() + File.separator + "audioquestion" + ".m4a";
		initRecordAudio(path);
		
	}

	void initRecordAudio(String storagePath)
    {
    	 recorder = new MediaRecorder();
    	 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	 recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    	 recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //3 is AAC
    	 recorder.setOutputFile(storagePath);
    	 try
    	 {
    		 recorder.prepare();
    	 }
    	 catch (IOException io)
    	 {
    		 Log.i("Preet", "recorder not initialized properly.");
    	 }
    	 
    }

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (touched == MotionEvent.ACTION_DOWN) //draw menu
		{
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	        paint.setColor(Color.WHITE);
	        paint.setTextAlign(Paint.Align.CENTER);
	        paint.setTypeface(Typeface.DEFAULT_BOLD);
	        
	        paint.setStrokeWidth(2);
	        paint.setTextSize(12); 
	        paint.setColor(Color.RED);
	        if (!startRec) //done prior to a_up hence in reverse
	        {
	        	canvas.drawText("START RECORDING", x_d, y_d,  paint); 
	        }
	        else
	        {
	        	canvas.drawText("STOP", x_d, y_d,  paint); 
	        }
	          
		}
		if (touched == MotionEvent.ACTION_UP)
		{
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	        paint.setColor(Color.WHITE);
	        paint.setTextAlign(Paint.Align.CENTER);
	        paint.setTypeface(Typeface.DEFAULT_BOLD);
	        
		}
		
		/*if (touched == MotionEvent.ACTION_MOVE)
		{
		  if (sector.equalsIgnoreCase("START RECORDING"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("START RECORDING", x_d, y_d-60,  paint); 
		        paint.setStrokeWidth(2);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED); 
		        canvas.drawText("STOP", x_d, y_d+60,  paint);
		        
		        
		        
		  }
		  if (sector.equalsIgnoreCase("STOP"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("STOP", x_d, y_d+60,  paint);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("START RECORDING", x_d, y_d-60,  paint); 
		        
		  }
		  
		} // END A_MOVE BLOCK
*/		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{//rtn true of event is handled
		// TODO Auto-generated method stub
		//return super.onTouchEvent(event);
		int action = event.getAction();  //Log.i("Preet", "Entered OnTouchEvent");
		if (action == MotionEvent.ACTION_DOWN) //when finger touches the screen
		{
			touched = action;
			x_d = event.getX(); y_d = event.getY();
			invalidate();
			if (!startRec) sector = "START RECORDING"; //again, this is prior to the startRec flag change, so we reverse the polarity of the flag
			else sector = "STOP";
			new Thread(this).start();
		}
		/*if  (action == MotionEvent.ACTION_MOVE)
		{
			x_m = event.getX(); y_m = event.getY();
			//first check if A_Move is extreme enough (outside the 45X45 buffer zone) to count as a selection. 
			if ( (Math.abs(x_m - x_d) > 45.0) || (Math.abs(y_m - y_d) > 45.0) )
			{
				sector = getSector();
				if (selected_mItem == false)
					{
						selected_mItem = true;
						
						if (!sector.equalsIgnoreCase("Invalid quadrant"))
						{
							new Thread(this).start(); // no reusing thread
						}
						
					}
				
				
				touched = action;
				invalidate();
			}
			else //redraw original menu
			{ 
				selected_mItem = false;
				touched = MotionEvent.ACTION_DOWN;
				invalidate();
			}
		} // end a_move
*/		
		if (action == MotionEvent.ACTION_UP)//when your finger lifts up from screen after touching. 
			//naturally must be preceded by Action Down, and optionally by Action Move. 
		{ //case 1 : No menu item selected, therefore erase menu (i.e., go to onDraw)
			/*if (!selected_mItem)
			{
				touched = action;
				invalidate();
			}
			else
			{*/
				touched = action;
				startRec = !startRec;
				invalidate();
				launchMItem() ;
			//}
		}
		
		return true;
		
	}
	
	void launchMItem()
	{
		if (startRec)
		{
			startRecordAudio();
		}
		if (!startRec)
		{
			stopRecordAudio();
		}
		
		/*Toast msg = Toast.makeText(this.getContext(), "You have selected " + sector + "! ", Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
		msg.show();
		
		*/
	}
	void startRecordAudio()
    {
		try
		{
			Log.i("Preet","startRecordAudio started");
	    	recorder.start();
		}
		catch (Exception e)
		{
			Log.i("Preet", "Mediarecorder died with error:  " + e.getMessage());
		}
    }
	
	void stopRecordAudio()
	{
		try
		{
			recorder.stop();
		}
		catch (Exception e)
		{
			Log.i("Preet","This was fatal prev.");
			e.printStackTrace();
		}
		
		//other stuff
		//recorder.release();
		Log.i("Preet","About to launch return Intent");
        Intent i = new Intent(parent, SendMenuShell.class);
        recorder.release();
        parent.startActivity(i);
		
	}
	
	String getSector()
	{
		//String sector_names[] = {"Up", "Down", "Left", "Right"};
		int quadrant = 0;		
		//get offsets
		x_o = x_m - x_d; y_o = y_m - y_d;
		
		if (x_o < 0 && y_o < 0)
			quadrant = 4;
		if (x_o < 0 && y_o > 0)
			quadrant = 3;
		if (x_o < 0 && y_o > 0)
			quadrant = 3;
		if (x_o > 0 && y_o < 0)
			quadrant = 1;
		if (x_o > 0 && y_o > 0)
			quadrant = 2;
		
		switch(quadrant)
		{
			case 1:
				if ( x_o <= Math.abs(y_o) )
					return "START RECORDING";
				/*else
					return "twitter";*/
			case 2: 
				if ( x_o <= y_o )
					return "STOP";
				/*else
					return "twitter";*/
			case 3:
				if ( Math.abs(x_o) <= y_o )
					return "STOP";
				/*else
					return "EMAIL";*/
			/*case 4:
				if (x_o <= y_o )
					return "EMAIL";
				else
					return "START RECORDING";*/
			default: return "Invalid quadrant";	
		
		}
		
	}
	void launchSpeakSelectedItem()
	{
		
	}

	@Override
	public void run() {
		Log.i("Preet", "Thread " + ++ctr_threads + " is running.");
		parent.v.vibrate(300);
		parent.titus.speak(sector, TextToSpeech.QUEUE_FLUSH, null);
		
	}
	
	
}




