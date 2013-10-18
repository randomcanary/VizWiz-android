package com.VizWiz.TestRelUI;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

public class TakePicActivity extends Activity {

    private Preview mPreview;
    public String queryID = null;
    TextToSpeech titus;
    MediaRecorder recorder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        Log.i("Preet","About to create Preview");
        // Create our Preview view and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);                    

        
    }
    
    public void callRecordAudioActivity() {
	    //setResult(RESULT_OK);
	    //finish();  
    	
    	//this is to prevent memleaks from bitmaps remaining in mem
    	mPreview.bitmapPicture.recycle();
    	System.gc();
    	
    	Log.i("Preet","About to launch return Intent");
        Intent i = new Intent(this, RecAudioShell.class);
       /* if (queryID != null)
                i.putExtra(VWLauncher.QUERY_ID, queryID);
        else        
                Log.i("Preet","Error - queryID is null");*/
        startActivity(i);
    }
    void initRecordAudio(String storagePath)
    {
    	 recorder = new MediaRecorder();
    	 recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	 recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    	 recorder.setAudioEncoder(3); //3 is AAC
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
    void startRecordAudio()
    {
    	recorder.start();
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
    

// Inner Class
class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    Context mContext;
    Bitmap bitmapPicture; 
    String uniqueID, result;
    public boolean completed = false;

    Preview(Context context) {
        super(context);
        mContext = context;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);    
        uniqueID = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);              
    }
    
    ShutterCallback myShutterCallback = new ShutterCallback(){

    	 //@Override
    	 public void onShutter() {
    		 // TODO Auto-generated method stub
    	 
    	 }};

	PictureCallback myPictureCallback_RAW = new PictureCallback(){

		 //@Override
		 public void onPictureTaken(byte[] arg0, Camera arg1) {
			 // TODO Auto-generated method stub	 
			 
		 }};    

    PictureCallback myPictureCallback_JPG = new PictureCallback(){
    	
    	public void onPictureTaken(byte[] arg0, Camera arg1) {    		
            FileOutputStream fileOutputStream = null;
            String path;    		
            
	    	bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length); 
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
    		
            try {
            	//String uniqueID = Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID);
            	if (uniqueID == null) {
            		uniqueID = "Preet_test1234";
            	}

            	fileOutputStream = new FileOutputStream(routesRoot.toString() + File.separator + "Picture.jpg");
                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);     
                bitmapPicture.compress(CompressFormat.JPEG, 50, bos);
                bos.flush();
                bos.close();
                
                //VWuploadFile(); this func is implemented post SendMenuView
            }
	        catch (Exception e) {
	            // TODO Auto-generated catch block
	        	Log.i("Preet","Exception saving file");
	            e.printStackTrace();
	        }  
	        Log.i("Preet","Calling returnActivity from onPictureTaken");
	        
	        //now that the pic has been taken and stored, we record the audio question  
	        TakePicActivity.this.initRecordAudio(path); //initializes the audio recorder
	       // announceRecordAudio(); // says "Record your question."
	       // TakePicActivity.this.startRecordAudio();
	        
	        TakePicActivity.this.callRecordAudioActivity();	        
    	}
    };         

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where to draw.
        mCamera = Camera.open();
        try {
           mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
            // TODO: add more exception handling logic here
        }
    }

    protected void announceRecordAudio() 
    {
    	TakePicActivity.this.titus = new TextToSpeech(TakePicActivity.this, TakePicActivity.this.ttsInitListener);
    	TakePicActivity.this.titus.speak("Record your question.", TextToSpeech.QUEUE_FLUSH, null);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();

        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, w, h);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);

        mCamera.setParameters(parameters);
        mCamera.startPreview();

        mCamera.takePicture(myShutterCallback,myPictureCallback_RAW, myPictureCallback_JPG);	 
    }
    
    public void VWuploadFile() {
    	String temp = "";
    	
    	VWLaunchHTTP();
    	try{
    		temp = VWexecuteHttpPost();
    		TakePicActivity.this.queryID = readJSONData(temp);
    	}
    	catch (Exception e) {
    		Log.i("Preet", "Exception from HTTP Post");
    		e.printStackTrace();
    	}
    	Log.i("Preet", "Our result is " + temp);
    	
    }
    
    public void VWLaunchHTTP() {
    	Log.i("Preet","Entered VWLaunchHTTP");
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet();
        try {
        	httpget.setURI(new URI("http://vizwiz-social.appspot.com/clientactivity"));
        }
        catch (URISyntaxException e1) {
        	
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

    public String VWexecuteHttpPost() throws Exception {
    	String picpath = "";
    	Log.i("Preet", "Entered executeHttpPost");
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();           
            HttpPost request = new HttpPost(result);
            
            Log.i("Preet", "created HttpClient and HttpPost");
            //pick up file from local temp dir and upload it
            picpath = Environment.getExternalStorageDirectory().toString() + File.separator + "test" + File.separator + uniqueID + ".jpg";
	        ContentBody bin = new FileBody(new File(picpath), "image/jpeg");  
	        
	        	
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);    
            Log.i("Preet", "Created MultipartEntity");
            reqEntity.addPart("hardwareId", new StringBody(uniqueID));   
            Log.i("Preet", "Added hardwareId of "+uniqueID);
            reqEntity.addPart("image", bin);  
            reqEntity.addPart("mturk", new StringBody("true"));
            Log.i("Preet", "Added image file of "+uniqueID);
      
            request.setEntity(reqEntity);
            request.setURI(new URI(result));

            Log.i("Preet", "about to call execute");
            HttpResponse response = client.execute(request);
            
            
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String result1 = sb.toString();
            return result1;
            } finally {
            if (in != null) {
            	Log.i("Preet", "In the finally");
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
    }   
    
    public String readJSONData(String s) throws Exception
    {
    	JSONObject json = new JSONObject();
    	json=new JSONObject(s);
    	String val = json.get("queryId").toString();
    	Log.i("Preet", "The json value of queryId is " + val);
    	return val;
    }
    
    
}//end inner class


}
