package com.VizWiz.TestRelUI;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class TryAgain extends TextView implements Runnable {
	int touched = 0; //indicates that the screen has been touched, so can draw menu then. 
	float x_d, y_d, x_m, y_m, x_o, y_o; //d indicates x,y for A_Down, m for A_Move, and o, the offset (x_m-x_d)
	boolean selected_mItem = false;
	int ctr_threads = 0;
	//String selected;
	String sector; 
	private SendMenuShell parent;
	Thread gThread;
	String extraStuff = "";
	boolean recdQuery = false;
	String result = "";
	String resp_time, resp_resp;

	public TryAgain(Context context) {
		super(context);
		parent = ((SendMenuShell) context);
		gThread = new Thread(this);
		

		//Log.i("Preet", "TryAgain constructor finished");
		// The Activity SendMenuShell (the equivalent of MarvinInitMenuShell) must be linked here. 
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
	        canvas.drawText("Get MORE ANSWERS", x_d, y_d-60,  paint); 
	        canvas.drawText("NEXT ANSWER", x_d+60, y_d,  paint);
	        canvas.drawText("MAIN MENU", x_d, y_d+60,  paint);
	        canvas.drawText("PREVIOUS ANSWER", x_d-60, y_d,  paint);       
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
		  if (sector.equalsIgnoreCase("Get MORE ANSWERS"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("Get MORE ANSWERS", x_d, y_d-60,  paint); 
		        paint.setStrokeWidth(2);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("NEXT ANSWER", x_d+60, y_d,  paint);
		        canvas.drawText("MAIN MENU", x_d, y_d+60,  paint);
		        canvas.drawText("PREVIOUS ANSWER", x_d-60, y_d,  paint);       
		        
		        
		  }
		  if (sector.equalsIgnoreCase("MAIN MENU"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("MAIN MENU", x_d, y_d+60,  paint);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("NEXT ANSWER", x_d+60, y_d,  paint);
		        canvas.drawText("Get MORE ANSWERS", x_d, y_d-60,  paint); 
		        canvas.drawText("PREVIOUS ANSWER", x_d-60, y_d,  paint);       
		        
		  }
		  if (sector.equalsIgnoreCase("PREVIOUS ANSWER"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("PREVIOUS ANSWER", x_d-60, y_d,  paint);   
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("NEXT ANSWER", x_d+60, y_d,  paint);
		        canvas.drawText("Get MORE ANSWERS", x_d, y_d-60,  paint); 
		        canvas.drawText("MAIN MENU", x_d, y_d+60,  paint);    
		  }
		  if (sector.equalsIgnoreCase("NEXT ANSWER"))
		  {
			  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		        paint.setColor(Color.WHITE);
		        paint.setTextAlign(Paint.Align.CENTER);
		        paint.setTypeface(Typeface.DEFAULT_BOLD);
		        
		        paint.setStrokeWidth(2);
		        paint.setTextSize(15); 
		        paint.setColor(Color.GREEN);
		        canvas.drawText("NEXT ANSWER", x_d+60, y_d,  paint);
		        paint.setTextSize(12); 
		        paint.setColor(Color.RED);
		        canvas.drawText("PREVIOUS ANSWER", x_d-60, y_d,  paint);   
		        canvas.drawText("Get MORE ANSWERS", x_d, y_d-60,  paint); 
		        canvas.drawText("MAIN MENU", x_d, y_d+60,  paint);    
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
			if ( (Math.abs(x_m - x_d) > 45.0) || (Math.abs(y_m - y_d) > 45.0) )
			{
				sector = getSector();
				if (selected_mItem == false)
					{
						selected_mItem = true;
						new Thread(this).start(); // no reusing thread
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
		}
		
		if (action == MotionEvent.ACTION_UP)//when your finger lifts up from screen after touching. 
			//naturally must be preceded by Action Down, and optionally by Action Move. 
		{ //case 1 : No menu item selected, therefore erase menu (i.e., go to onDraw)
			if (!selected_mItem)
			{
				touched = action;
				invalidate();
			}
			else
			{
				launchMItem(sector) ;
			}
		}
		
		return true;
		
	}
	
	void launchMItem(String sector)
	{
		Log.i("Preet", "Sector is : " + sector);
		if (sector.equalsIgnoreCase("Get MORE ANSWERS"))
		{
			parent.titus.speak("Getting answers.",  TextToSpeech.QUEUE_FLUSH, null);
			parent.keepChecking = true;
			new Thread(parent).start();
			
		}
		else if (sector.equalsIgnoreCase("NEXT ANSWER"))
		{
			String s = "";
			try {
				s = parent.getTextfromJSONnext();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("Preet", "getTextfromJSONnext failed");
			}
			parent.titus.speak(s, TextToSpeech.QUEUE_FLUSH, null);
		}
		else if (sector.equalsIgnoreCase("PREVIOUS ANSWER"))
		{
			String s = "";
			try {
				s = parent.getTextfromJSONprev();
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("Preet", "getTextfromJSONprev failed");
			}
			parent.titus.speak(s, TextToSpeech.QUEUE_FLUSH, null);
		}
		else // go to main menu
		{
			parent.titus.speak("Going to main menu", TextToSpeech.QUEUE_FLUSH, null);
			Intent i = new Intent(parent, InitMenuShell.class);
	        parent.startActivity(i);	
		}
		
		/*Toast msg = Toast.makeText(this.getContext(), "You have selected " + sector + "! ", Toast.LENGTH_LONG);
		msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
		msg.show();
		
		*/
	}
	
	private void launchChkResponse() 
	{}
	
	public void VWLaunchHTTP(String s) {
    	Log.i("Preet","Entered VWLaunchHTTP");
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
					return "Get MORE ANSWERS";
				else
					return "NEXT ANSWER";
			case 2: 
				if ( x_o <= y_o )
					return "MAIN MENU";
				else
					return "NEXT ANSWER";
			case 3:
				if ( Math.abs(x_o) <= y_o )
					return "MAIN MENU";
				else
					return "PREVIOUS ANSWER";
			case 4:
				if (x_o <= y_o )
					return "PREVIOUS ANSWER";
				else
					return "Get MORE ANSWERS";
			default: return "Invalid quadrant";	
		
		}
		
	}
	void launchSpeakSelectedItem()
	{
		
	}

	@Override
	public void run() {
		Log.i("Preet", "Thread " + ++ctr_threads + " is running.");
		if (!recdQuery)
		{
			parent.v.vibrate(300);
			parent.titus.speak(sector.concat(extraStuff), TextToSpeech.QUEUE_FLUSH, null);
			extraStuff = "";
		}
		else
		{
			parent.v.vibrate(300);
			parent.titus.speak("Response : " + resp_resp + " received at " + resp_time, TextToSpeech.QUEUE_FLUSH, null);
			recdQuery = false;
		}
		
	}
	public void takePic() {
        Log.i("Preet", "Click Picture Button Clicked");
        Intent i = new Intent(parent, TakePicActivity.class);
        //startActivityForResult(i, CAMERA_VIEW);
        parent.startActivity(i);
	}
	public String readJSONData(String s, String keyword) throws Exception
    {
    	JSONObject json = new JSONObject();
    	json=new JSONObject(s);
    	String val = json.get(keyword).toString();
    	Log.i("Preet", "The json value of queryId is " + val);
    	return val;
    }
	
	
}




