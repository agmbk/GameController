package com.example.gamecontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class BasicVehicle extends AppCompatActivity {

    private final SomeAncientVehicle vehicule = new SomeAncientVehicle();
    private Button rightButton;
    private Button leftButton;
    private Button forwardButton;
    private Button backwardButton;
    private Button gameExitButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_button_controls);

        GameMessageManager.logActivity(true);
        rightButton = findViewById(R.id.rightButton);

        rightButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("pressed");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vehicule.setRightPower(vehicule.FORWARD);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        vehicule.setRightPower(vehicule.STOP);
                        break;
                }
                return true;
            }
        });

        leftButton = findViewById(R.id.leftButton);
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("pressed");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vehicule.setLeftPower(vehicule.FORWARD);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        vehicule.setLeftPower(vehicule.STOP);
                        break;
                }
                return true;
            }
        });

        forwardButton = findViewById(R.id.forwardButton);
        forwardButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("shoot");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        vehicule.killThatEnemy();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        vehicule.spareTheBullets();
                        break;
                }
                return true;
            }
        });

        backwardButton = findViewById(R.id.backwardButton);
        //backwardButton.setOnClickListener(this::turnLeftEvent);

        gameExitButton = findViewById(R.id.gameExit);
        gameExitButton.setOnClickListener(this::exitButton);

        vehicule.start();
    }

    public void exitButton(View view) {
        this.exit();
    }

    public void exit() {
        GameMessageManager.sendMessage("EXIT");
        finish();
    }
}


class SomeAncientVehicle extends Thread {
    public final double FORWARD = 1;
    public final double STOP = 0.5;
    public final double REVERSE = 0;

    private final double FALLING_ASLEEP = 8000;
    private final String COFFEE = "LIVE";
    private boolean newDrivingInstructions = false;
    private long driverLastCoffee = new Date().getTime();
    private double leftPower = 0.5;
    private double rightPower = 0.5;
    private boolean isShooting = false;

    public void setLeftPower(double leftPower) {
        if (this.leftPower != leftPower) {
            this.leftPower = leftPower;
            newDrivingInstructions = true;
        }
    }

    public void setRightPower(double rightPower) {
        if (this.rightPower != rightPower) {
            this.rightPower = rightPower;
            newDrivingInstructions = true;
        }
    }

    public void killThatEnemy() {
        if (!this.isShooting) {
            this.isShooting = true;
            newDrivingInstructions = true;
        }
    }

    public void spareTheBullets() {
        if (this.isShooting) {
            this.isShooting = false;
            newDrivingInstructions = true;
        }
    }

    public void run() {
        while (!GameMessageManager.isConnected()) {
            Log.i("CAR STATUS", "WARMING UP THE ENGINE ...");
            this.takeABreak(500);
        }

        Log.i("CAR STATUS", "LET'S START REEVING IT UP");

        GameMessageManager.sendMessage("NAME=NEVER GONNA GIVE UP");

        while (GameMessageManager.isConnected()) {
            /*String gameInfo = GameMessageManager.getNextMessage();

            if (gameInfo != null) {
                Log.i("GAME INFO", gameInfo);
            }
            */

            Log.i("VEHICLE STATUS", "MotL=" + leftPower + "#MotR=" + rightPower + "#GunTrig=" + (isShooting ? 1 : 0));

            if (newDrivingInstructions)
                sendDrivingInstructions("MotL=" + leftPower + "#MotR=" + rightPower + "#GunTrig=" + (isShooting ? 1 : 0));
            else
                keepTheDriverAwake();

            this.takeABreak(100);
        }

        Log.i("CAR STATUS", "GUN IS JAMMED ! OR AN ACCIDENT MAYBE, WE'LL NEVER KNOW ...");
    }

    private void sendDrivingInstructions(String message) {
        GameMessageManager.sendMessage(message);
        tastyCoffee();
    }

    private void drinkACoffee() {
        sendDrivingInstructions(COFFEE);
        tastyCoffee();
    }

    private void tastyCoffee() {
        driverLastCoffee = new Date().getTime();
    }

    private void keepTheDriverAwake() {
        if (new Date().getTime() - driverLastCoffee >= FALLING_ASLEEP)
            drinkACoffee();
    }

    private void takeABreak(int milliseconds) {
        try {
            if (milliseconds > FALLING_ASLEEP) milliseconds = (int) FALLING_ASLEEP;
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}