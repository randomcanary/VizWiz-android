package com.VizWiz.TestRelUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class SendMenuView extends TextView implements Runnable {
	int touched = 0; //indicates that the screen has been touched, so can draw menu then. 
	float x_d, y_d, x_m, y_m, x_o, y_o; //d indicates x,y for A_Down, m for A_Move, and o, the offset (x_m-x_d)
	boolean selected_mItem = false;
	int ctr_threads = 0;
	//String selected;
	String sector; 
	private SendMenuShell parent;
	Thread gThread;
	Choices choices = new Choices();
	String extraStuff = "";
	// added from UploadFile stuff
	String uniqueID, result;
	int logCtr = 0; //for log.i
	public String queryID = null;
	
	public SendMenuView(Context context) {
		super(context);
		parent = ((SendMenuShell) context);
		gThread = new Thread(this);
		//choices.add(send); choices.add(Web_Workers); choices.add(twitter); choices.add(I_Q_Engines);

		//Log.i("Preet", "InitMenu constructor finished");
		// The Activity InitMenuShell (the equivalent of MarvinInitMenuShell) must be linked here. 
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
	        canvas.drawText("SEND", x_d, y_d-60,  paint); 
	        canvas.drawText("twitter", x_d+60, y_d,  paint);
	        canvas.drawText("I Q Engines", x_d, y_d+60,  paint);
	        canvas.drawText("Web Workers", x_d-60, y_d,  paint);       
		}
		if (touched == MotionEvent.ACTION_UP)
		{
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	        paint.setColor(Color.WHITE);
	        paint.setTextAlign(Paint.Align.CENTER);
	        paint.setTypeface(Typeface.DEFAULT_BOLD);
	        
		}
		
		if (touched == MotionEvent.ACTION_MOVE)
		{
		  if (sector.equalsIgnoreCase("SEND"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("SEND", x_d, y_d-60,  paint); 
		        paint.setStrokeWidth(2);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("twitter", x_d+60, y_d,  paint);
		        canvas.drawText("I Q Engines", x_d, y_d+60,  paint);
		        canvas.drawText("Web Workers", x_d-60, y_d,  paint);       
		        
		        
		  }
		  if (sector.equalsIgnoreCase("I Q Engines"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("I Q Engines", x_d, y_d+60,  paint);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("twitter", x_d+60, y_d,  paint);
		        canvas.drawText("SEND", x_d, y_d-60,  paint); 
		        canvas.drawText("Web Workers", x_d-60, y_d,  paint);       
		        
		  }
		  if (sector.equalsIgnoreCase("Web Workers"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("Web Workers", x_d-60, y_d,  paint);   
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("twitter", x_d+60, y_d,  paint);
		        canvas.drawText("SEND", x_d, y_d-60,  paint); 
		        canvas.drawText("I Q Engines", x_d, y_d+60,  paint);    
		  }
		  if (sector.equalsIgnoreCase("twitter"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("twitter", x_d+60, y_d,  paint);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("Web Workers", x_d-60, y_d,  paint);   
		        canvas.drawText("SEND", x_d, y_d-60,  paint); 
		        canvas.drawText("I Q Engines", x_d, y_d+60,  paint);    
		  }
		  
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {//rtn true of event is handled
		// TODO Auto-generated method stub
		//return super.onTouchEvent(event);
		int action = event.getAction();  //Log.i("Preet", "Entered OnTouchEvent");
		if (action == MotionEvent.ACTION_DOWN) //when finger touches the screen
		{
			touched = action;
			x_d = event.getX(); y_d = event.getY();
			invalidate();
		}
		if  (action == MotionEvent.ACTION_MOVE)
		{
			x_m = event.getX(); y_m = event.getY();
			//first check if A_Move is extreme enough (outside the 45X45 buffer zone) to count as a selection. 
			if ( (Math.abs(x_m - x_d) > 45.0) || (Math.abs(y_m - y_d) > 45.0) )//take this out if you don't
				//want "INVALID QUADRANT"
			{
				sector = getSector();
				if (selected_mItem == false)
					{
						selected_mItem = true;
						extraStuff = "";
						new Thread(this).start(); // no reusing thread
					}
				touched = action;
				invalidate();
			}
			else //redraw original menu in invalid quadrant case
			{ 
				
				selected_mItem = false;
				touched = MotionEvent.ACTION_DOWN;
				invalidate();
			}
		}
		
		if (action == MotionEvent.ACTION_UP)//when your finger lifts up from screen after touching. 
			//naturally must be preceded by Action Down, and optionally by Action Move. 
		{ //case 1 : No menu item selected, therefore erase menu (i.e., go to onDraw)
			if (!selected_mItem)
			{
				touched = action;
				invalidate();
			}
			else //selected_mItem is true, use sector instead of booleans? 
			{
				sector = getSector();
				launchMItem() ;
				selected_mItem = false;
			}
		}
		
		return true;
		
	}
	
	void launchMItem()
	{
		Log.i("Preet", "Sector is : " + sector);
		//first, launchmItem toggles the corresponding switch
		Log.i("Preet", "Pre-toggle value for boolean " + sector + " is : " + choices.getChoice(sector));
		choices.toggleChoice(sector);
		Log.i("Preet", "Post-toggle value for boolean " + sector + " is : " + choices.getChoice(sector));
		
		//second, do various stuff
		if (sector.equalsIgnoreCase("send"))
		{
			Log.i("Preet", "choices.anythingSelected() : " + choices.anythingSelected());
			if (choices.anythingSelected())
			{
				String c1 = (choices.I_Q_Engines)? " IQ E