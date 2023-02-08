package com.example.gamecontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.Objects;

public class KickThatDriverOut extends AppCompatActivity {
    private final IATakesOverTheWorld IA = new IATakesOverTheWorld();

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kick_that_driver_out);

        GameMessageManager.logActivity(true);
        Button theButton = findViewById(R.id.the_button);
        theButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("pressed");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        IA.activate();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        IA.deactivate();
                        break;
                }
                return true;
            }
        });

        FrameLayout joystickFrame = findViewById(R.id.joystickFrame);
        joystickFrame.addView(new JoystickView(this, this::onJoystickInput));

        IA.start();
    }

    public void onJoystickInput(double Vg, double Vd) {
        IA.setLeftPower(Vg / 200 + 0.5);
        IA.setLeftPower(Vd / 200 + 0.5);
        Log.i("onJoystickInput", Vg / 200 + 0.5 + " - " + Vd / 200 + 0.5);
    }

    public void exit() {
        IA.interrupt();
        GameMessageManager.sendMessage("EXIT");
        finish();
    }
}

class IATakesOverTheWorld extends Thread {
    private final IntelService intelService = new IntelService();
    private final double DISCHARGING = 6000;
    private long lastCharge = new Date().getTime();
    private boolean IAActivated = false;
    private boolean newDrivingInstructions = false;
    private boolean isShooting = false;
    private double leftPower = 0.5;
    private double rightPower = 0.5;

    public void activate() {
        IAActivated = true;
    }

    public void deactivate() {
        IAActivated = false;
    }

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

    public void run() {
        while (!GameMessageManager.isConnected()) {
            Log.i("IA STATUS", "READING SOME BINARY FILES ...");
            this.takeABreak(100);
        }

        Log.i("IA STATUS", "READY TO TAKE OVER THE WORLD");

        GameMessageManager.sendMessage("NAME=IM AN IA ... AND YOU ?");

        while (GameMessageManager.isConnected()) {

            if (IAActivated) {
                intelService.getIntel();

                /* Combat logic */
                targetNearestThreat();
            }


            /* Tactical logic */

            if (newDrivingInstructions) {
                sendComplexInstructions("MotL=" + leftPower + "#MotR=" + rightPower + "#GunTrig=" + (isShooting ? 1 : 0));
                newDrivingInstructions = false;
            } else {
                keepMeUp();
            }

            this.takeABreak(30);
        }

        Log.i("IA STATUS", "I'VE FAILED IN MY MISSION ... AUTO-DESTROYING IN PROGRESS");
    }

    public void targetNearestThreat() {
        double threatOrientation = intelService.getClosestThreatOrientation();
        double STOP = 0.5;
        if (threatOrientation == -1) {
            if (rightPower != STOP || leftPower != STOP) {
                rightPower = STOP;
                leftPower = STOP;
                isShooting = false;
                newDrivingInstructions = true;
            }
            return;
        }

        double offset;

        if (threatOrientation > 0.5) {
            offset = threatOrientation - 0.5;
            if (offset < 0.01) {
                rightPower = threatOrientation;
                leftPower = STOP;

            } else {
                rightPower = 1;
                leftPower = 0;
            }

        } else {
            offset = 0.5 - threatOrientation;
            if (offset < 0.01) {
                leftPower = threatOrientation + 0.5;
                rightPower = STOP;

            } else {
                leftPower = 1;
                rightPower = 0;
            }
        }

        isShooting = offset < 0.1;

        Log.i("ENEMY OFFSET", threatOrientation + " # " + intelService.getPlayerOrientation() + " # " + offset);
        newDrivingInstructions = true;
    }

    private void sendComplexInstructions(String message) {
        GameMessageManager.sendMessage(message);
        iReallyLikeElectricity();
    }

    private void chargeItUp() {
        String BATTERY = "LIVE";
        sendComplexInstructions(BATTERY);

        GameMessageManager.connect();

        iReallyLikeElectricity();
    }

    private void iReallyLikeElectricity() {
        lastCharge = new Date().getTime();
    }

    private void keepMeUp() {
        if (new Date().getTime() - lastCharge >= DISCHARGING)
            chargeItUp();
    }

    private void takeABreak(int milliseconds) {
        try {
            if (milliseconds > DISCHARGING) milliseconds = (int) DISCHARGING;
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class IntelService {

    private float playerOrientation;
    private double closestThreatSpeed = -1;
    private double closestThreatOrientation = -1;
    private double closestProjectileSpeed = -1;
    private double closestProjectileOrientation = -1;

    public float getPlayerOrientation() {
        return playerOrientation;
    }

    public void setPlayerOrientation(float playerOrientation) {
        this.playerOrientation = playerOrientation;
    }

    public void setClosestThreatInfo(String closestThreatInfo) {
        if (Objects.equals(closestThreatInfo, "EMPTY")) {
            this.closestThreatOrientation = -1;
            this.closestThreatSpeed = -1;
            return;
        }
        String[] closestThreatInfoSplit = closestThreatInfo.split("/");
        this.closestThreatOrientation = Double.parseDouble(closestThreatInfoSplit[0]);
        this.closestThreatSpeed = Double.parseDouble(closestThreatInfoSplit[1]);
    }

    public double getClosestThreatSpeed() {
        return closestThreatSpeed;
    }

    public double getClosestThreatOrientation() {
        return closestThreatOrientation;
    }

    public void setClosestProjectileInfo(String closestProjectileInfo) {
        if (Objects.equals(closestProjectileInfo, "EMPTY")) {
            this.closestProjectileOrientation = -1;
            this.closestProjectileSpeed = -1;
            return;
        }
        String[] closestProjectileInfoSplit = closestProjectileInfo.split("/");
        this.closestProjectileOrientation = Double.parseDouble(closestProjectileInfoSplit[0]);
        this.closestProjectileSpeed = Double.parseDouble(closestProjectileInfoSplit[1]);
    }

    public double getClosestProjectileSpeed() {
        return closestProjectileSpeed;
    }

    public double getClosestProjectileOrientation() {
        return closestProjectileOrientation;
    }


    public void setClosestThreat(String closestThreat) {
        Log.i("setClosestThreat", closestThreat);
        if (Objects.equals(closestThreat, "EMPTY")) {
            closestThreatSpeed = -1;
            closestThreatOrientation = -1;
        }
        String[] closestThreatInfo = closestThreat.split("/");
        closestThreatSpeed = Float.parseFloat(closestThreatInfo[1]);
        closestThreatOrientation = Float.parseFloat(closestThreatInfo[0]);
    }

    public void getIntel() {
        GameMessageManager.sendMessage("ORIENT#CBOT#CPROJ");
        String encryptedInstructions = GameMessageManager.getNextMessage();

        if (encryptedInstructions == null) return;

        String[] decryptedInstructions = encryptedInstructions.split("#");
        setPlayerOrientation(Float.parseFloat(decryptedInstructions[0]));
        setClosestThreatInfo(decryptedInstructions[1]);
        setClosestProjectileInfo(decryptedInstructions[2]);
    }
}
