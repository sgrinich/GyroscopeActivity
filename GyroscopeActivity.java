package com.example.sovereignty;

//Written by Stephen Grinich. 

//class activity for a gyrsocope view 

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;

public class GyroscopeActivity extends Activity implements SensorEventListener {

	float xG; 
	float yG; 
	float zG; 
	float xO; 
	float yO;
	float zO;
	float xMF; 
	float yMF;
	float zMF; 
	float xRV; 
	float yRV;
	float zRV; 
	
	int draw; 
	int check; 
	private int onOff;	
	private static final int LINE_SPACING = 220; 
	
	float centerx=640; 
	float centery=400;
	float northX; 
	float northY; 
	float oldcenterx; 
	float newcenterx; 
	float middlePoint;
	
	Button nextButton; 
	Button calibrate; 
	
	 public class CustomDrawableView extends View {
		    Paint bPaint = new Paint();
		    Paint oPaint = new Paint(); 
		    Paint textPaint = new Paint();
		    public CustomDrawableView(Context context) {
			    super(context);
			    bPaint.setColor(Color.BLACK);
			    bPaint.setStyle(Style.STROKE);	
			    bPaint.setStrokeWidth(4);
			    bPaint.setAntiAlias(true);
			      
			    oPaint.setColor(Color.parseColor("#ff7f00"));
			    oPaint.setStrokeWidth(2);
			      		      
			    textPaint.setStyle(Style.STROKE);
			    textPaint.setTextSize(50);
			    textPaint.setAntiAlias(true);
			    textPaint.setColor(Color.parseColor("#ff7f00"));
		        
		    };
		 
		    protected void onDraw(Canvas canvas) {
		    	int width = getWidth(); 
		    	int height = getHeight();
		    	
		    	// These three if statements make sure the user holds the device at a 90 degree angle vertically
		    	// before drawing any lines
			    if((zO >87.5) && (zO<92.5)){
			    	onOff = 1; 
			    }
			    // Change zO < 87.5 instead of zO <85.85 for a more "90 degree" start, but doesn't start as easily that way		
			    // This creates a "sliding down" effect of the text as the user rotates the device to be at an upward 90 degree angle
			    if( ((zO < 85.5) || (zO>92.5)) && onOff != 1 ){
			    	
			    	if(zO < 0){
			    		canvas.drawText("To enter gyroscopic mode,", 250, 200, textPaint);
					    canvas.drawText("please hold device vertically", 250, 300, textPaint);
			    	}
			    	
			    	
			    	for(int i=0;i<91;i++){
					    
			    		if(zO>=i && zO<i+1){
			    			canvas.drawText("To enter gyroscopic mode,", 250, 200+(i*3), textPaint);
						    canvas.drawText("please hold device vertically", 250, 300+(i*3), textPaint);
			    			
			    		}
			    		
			    	}		    	
			    }
			    if(onOff == 1){
		    	
		        //Loop through to create 10 vertical lines
			    for(int i = 1; i<11; i++){
			    	canvas.drawLine(centerx + (i*LINE_SPACING), -height, centerx + (i*LINE_SPACING), +height, oPaint);
	
			    }
			    for(int i = 1; i<11; i++){
			    	canvas.drawLine(centerx + (i*-LINE_SPACING), -height, centerx + (i*-LINE_SPACING), +height, oPaint);
			    	
			      }
			      
			    //Loop through to create 10 horizontal lines
			    for(int i = 1; i<11; i++){
				    canvas.drawLine(0, centery+(i*LINE_SPACING), width, centery+(i*LINE_SPACING), oPaint);
	
			    }
			    for(int i = 1; i<11; i++){
				    canvas.drawLine(0, centery-(i*LINE_SPACING), width, centery-(i*LINE_SPACING), oPaint);
	
			    }
			      
			    //middle vertical line from landscape point of view
			    canvas.drawLine(centerx, -getScreenY(), centerx, getScreenY(), bPaint);
			    //middle horizontal line from landscape point of view
			    canvas.drawLine(0, centery, getScreenX(), centery, bPaint);
			    //creates center point
			    //should probably use width and height instead of getScreenX and getScreenY
			    canvas.drawCircle((float)getScreenX()/2, (float)getScreenY()/2, (float)10, bPaint);
		    	}
		     
		       invalidate(); 
              
		    }
		  }
	
	CustomDrawableView mCustomDrawableView;
    private SensorManager mSensorManager; 
	private Sensor mGyroscope;
    private Sensor mOrientation;
	private Sensor mRotationVector;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			
		mCustomDrawableView = new CustomDrawableView(this);
	    setContentView(mCustomDrawableView); 
	    
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { 
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor; 

		if(sensor.getType() == Sensor.TYPE_ORIENTATION){
			xO = event.values[0];
			yO = event.values[1];
			zO = Math.round(event.values[2]);
		}
		
		
		if(sensor.getType() == Sensor.TYPE_GYROSCOPE){
			xG = event.values[0];	
			centerx = Math.round(centerx +(xG*10)); 
			yG = event.values[1];
			centery = Math.round(centery - (yG*10));
			
			// Snaps horizontal line to middle if devices is held at 90 degrees
			if((zO >87.5) && (zO<92.5)){
				if(centery != getScreenY()/2){
					centery = getScreenY()/2; 	
				}
				
			}
			
		}

	}

	protected void onResume(){
		super.onResume();
		mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener(this, mRotationVector, SensorManager.SENSOR_DELAY_FASTEST);
		
	}
	
	protected void onPause(){
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
	
	//Returns width of screen
	public float getScreenX(){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		return (float)width;
	}
	
	//Returns height of screen including any soft buttons
	public float getScreenY(){
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		
		
		return (float)height;
	}

}
