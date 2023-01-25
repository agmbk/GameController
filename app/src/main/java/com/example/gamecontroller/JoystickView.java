package com.example.gamecontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Displays a cursor in a circle. The cursor moves on touch events. Cursor position is
 * monitored and updated to determine two speeds suitable to control a robot.
 */
public class JoystickView extends View{

   private final Paint mCirclePaint;
   private final Paint mButtonPaint;
   private boolean mIsTouchDown = false;

   /**
    * Interface of the callback function that is called each time joystick value changes.
    */
   public interface ValueChangedHandler{
      void onValueChanged(int Vg, int Vd);
   }

   /**
    * Register a callback function to be called when value changes
    * @param pValueChangeHandler callback function called when value changes
    */
   public void setValueChangeHandler(ValueChangedHandler pValueChangeHandler) {
      this.mValueChangeHandler = pValueChangeHandler;
   }

   private int buttonCenterX = -1;
   private int buttonCenterY = -1;
   private int canvasCenterX = -1;
   private int canvasCenterY = -1;
   private static int sCircleRadius = 200;
   private static int buttonRadius = sCircleRadius / 10;

   private final static int sVMax = 100;
   private ValueChangedHandler mValueChangeHandler;

   public JoystickView(Context context, ValueChangedHandler pValueChangeHandler) {
      super(context);
      mCirclePaint = new Paint();
      mCirclePaint.setARGB(50, 200,200,200);
      mCirclePaint.setStyle(Paint.Style.STROKE);
      mCirclePaint.setStrokeWidth(5.0f);

      mButtonPaint = new Paint();

      this.mValueChangeHandler = pValueChangeHandler;
   }

   public void setJoystickRadius(int rad)
   {
      sCircleRadius = rad;
      buttonRadius = rad/10;
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      if (canvasCenterX == -1) {
         int w = canvas.getWidth();
         int h = canvas.getHeight();

         canvasCenterX = w / 2;
         canvasCenterY = h / 2;
      }

      canvas.drawCircle(canvasCenterX,canvasCenterY, sCircleRadius, mCirclePaint);

      if (buttonCenterX == -1)
      {
         buttonCenterX = canvasCenterX;
         buttonCenterY = canvasCenterY;
      }

      if (mIsTouchDown)
      {
         mButtonPaint.setStyle(Paint.Style.FILL_AND_STROKE);
         mButtonPaint.setARGB(255,41,148,255);
         canvas.drawCircle(buttonCenterX,buttonCenterY, 2*buttonRadius, mButtonPaint);
         mButtonPaint.setStyle(Paint.Style.STROKE);
         mButtonPaint.setARGB(50, 200,200,200);
         mButtonPaint.setStrokeWidth(5.0f);
         canvas.drawCircle(buttonCenterX,buttonCenterY, 2*buttonRadius+15, mButtonPaint);
      }
      else{
         mButtonPaint.setStyle(Paint.Style.FILL_AND_STROKE);
         mButtonPaint.setARGB(120, 41,148,255);
         mButtonPaint.setStrokeWidth(5.0f);
         canvas.drawCircle(buttonCenterX,buttonCenterY, buttonRadius, mButtonPaint);
      }
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      switch (event.getAction())
      {
         case MotionEvent.ACTION_DOWN:
         case MotionEvent.ACTION_MOVE:
            mIsTouchDown = true;
            buttonCenterX = Math.round(event.getX());
            buttonCenterY = Math.round(event.getY());
            final double X = buttonCenterX-canvasCenterX;
            final double Y = canvasCenterY-buttonCenterY; // Y axis is inverted in the View
            double Vg = Y + (X/2.0);
            double Vd = Y - (X/2.0);
            if (Y < 0){
               Vd = Vg;
               Vg = Y - X;
            }

            final double leftSpeed = (sVMax * Vg)/sCircleRadius;
            final int lSpeed;
            if (leftSpeed >= 0) {
               lSpeed = (int) Math.round(Math.min(sVMax, leftSpeed));
            } else {
               lSpeed = (int) Math.round(Math.max(-sVMax, leftSpeed));
            }

            final double rightSpeed = (sVMax * Vd)/sCircleRadius;
            final int rSpeed;
            if (rightSpeed >= 0) {
               rSpeed = (int) Math.round(Math.min(sVMax, rightSpeed));
            } else {
               rSpeed = (int) Math.round(Math.max(-sVMax, rightSpeed));
            }
            if (mValueChangeHandler != null) {
               // Invoke the callback function with computed values
               mValueChangeHandler.onValueChanged(lSpeed, rSpeed);
            }
            // Redraw with new button coordinate
            invalidate();
            return true;

         case MotionEvent.ACTION_UP:
            mIsTouchDown = false;
            buttonCenterX = canvasCenterX;
            buttonCenterY = canvasCenterY;
            if (mValueChangeHandler != null) {
               // Invoke the callback function to reset command
               mValueChangeHandler.onValueChanged(0, 0);
            }
            invalidate();
            return true;
         default:
            Log.i("Jostick:", "Joystick received event : "+ event.getAction());
      }

      return false;
   }

   public static int getsVMax()
   {
      return sVMax;
   }
}
