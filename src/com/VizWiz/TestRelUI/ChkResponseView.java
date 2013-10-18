package com.VizWiz.TestRelUI;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

public class ChkResponseView extends TextView implements Runnable
{
	private ChkResponseShell parent;
	String speechStuff = "Testing, testing, testing, testing, testing";
	
	public ChkResponseView(Context context) {
		super(context);
		parent = ((ChkResponseShell) context);
		//choices.add(send); choices.add(Web_Workers); choices.add(twitter); choices.add(I_Q_Engines);

		//Log.i("Preet", "InitMenu constructor finished");
		// The Activity InitMenuShell (the equivalent of MarvinInitMenuShell) must be linked here. 
	}

	@Override
	public void run() 
	{
		parent.v.vibrate(300);
		parent.titus.speak(speechStuff, TextToSpeech.QUEUE_FLUSH, null);
		
	}

}
