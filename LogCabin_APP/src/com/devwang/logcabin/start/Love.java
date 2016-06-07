package com.devwang.logcabin.start;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Love extends SurfaceView implements SurfaceHolder.Callback,  
Runnable{
	boolean mbloop = false;  
    SurfaceHolder mSurfaceHolder = null;  
    private Canvas canvas;  
    int miCount = 0;  
    int y = 50;  
  
    
    public Love(Context context) {  
        super(context);  
        mSurfaceHolder = this.getHolder();  
        mSurfaceHolder.addCallback(this);  
        this.setFocusable(true);  
        this.setKeepScreenOn(true);  
        mbloop = true;  
    }

	

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		 new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		mbloop = false; 
	}  
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 while (mbloop) {  
	            try {  
	               Thread.sleep(200);  
	            
	            } catch (Exception e) {  
	                // TODO: handle exception  
	            }  
	            synchronized (mSurfaceHolder) {  
	                Draw();  
	            }  
	        }  
	}



	private void Draw() {
		// TODO Auto-generated method stub
		 canvas = mSurfaceHolder.lockCanvas();  
	        try {  
	            if (mSurfaceHolder == null || canvas == null) {  
	                return;  
	            }  
	            if (miCount < 100) {  
	                miCount++;  
	            } else {  
	                miCount = 0;  
	            }  
	            Paint paint = new Paint();  
	            paint.setAntiAlias(true);  
	            paint.setColor(Color.BLACK);  
	           // canvas.drawRect(0, 0, 320, 480, paint); 
	            canvas.drawRect(0, 0, 1080, 1920, paint);  
	            switch (miCount % 6) {  
	            case 0:  
	                paint.setColor(Color.RED);  
	                break;  
	            case 1:  
	            	paint.setColor(Color.argb(255, 255, 125, 0));
	                break;  
	            case 2:  
	                paint.setColor(Color.YELLOW);  
	                break;  
	            case 3:  
	                paint.setColor(Color.GREEN);  
	                break;  
	            case 4:  
	            	paint.setColor(Color.BLUE); 
	                break;  
	            case 5:  
	                paint.setColor(Color.argb(255, 255, 0, 255));  
	                break;  
	            default:  
	                paint.setColor(Color.WHITE);  
	                break;  
	            }  
	            int i, j;  
	            double x, y, r;  
	  
	            for (i = 0; i <= 90; i++) {  //90度角
	               for (j = 0; j <= 90; j++) {  
	            
//	                    r = Math.PI / 45 * i * (1 - Math.sin(Math.PI / 45 * j))  
//	                            * 20;  
//	                    x = r * Math.cos(Math.PI / 45 * j)  
//	                            * Math.sin(Math.PI / 45 * i) + 320 / 2;  
//	                    y = -r * Math.sin(Math.PI / 45 * j) + 400 / 4;  
	                            r = Math.PI / 45 * i * (1 - Math.sin(Math.PI / 45 * j))  
	    	                            * 62.8;  
	                    x = r * Math.cos(Math.PI / 45 * j)  
	                            * Math.sin(Math.PI / 45 * i) + 1080 / 2;  
	                    y = -r * Math.sin(Math.PI / 45 * j) + 1920 / 4;  
	                    canvas.drawPoint((float) x, (float) y, paint);  
	                }  
	            }  
	  
	            paint.setTextSize(32);  
	            paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));  
	  
	         //   RectF rect = new RectF(60, 400, 260, 405);  
	        //    RectF rect = new RectF(0,1080, 0, 1920);//线
	       //     canvas.drawRoundRect(rect, (float) 1.0, (float) 1.0, paint);  
	           // canvas.drawText("Loving You", 75, 400, paint);
	            canvas.drawText("总有一种是您喜欢的颜色！！", 450, 1500, paint);
	           
	            mSurfaceHolder.unlockCanvasAndPost(canvas);  
	        } catch (Exception e) {  
	        }  
	  
	}
}
