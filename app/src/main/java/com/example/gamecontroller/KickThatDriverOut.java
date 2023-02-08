package com.example.gamecontroller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class KickThatDriverOut extends AppCompatActivity {
    private final AITakesOverTheWorld AI = new AITakesOverTheWorld();

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
                        AI.activate();
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        AI.deactivate();
                        break;
                }
                return true;
            }
        });

        FrameLayout joystickFrame = findViewById(R.id.joystickFrame);
        joystickFrame.addView(new JoystickView(this, this::onJoystickInput));

        AI.start();
    }

    public void onJoystickInput(double Vg, double Vd) {
        AI.setLeftPower(Vg / 200 + 0.5);
        AI.setLeftPower(Vd / 200 + 0.5);
        Log.i("onJoystickInput", Vg / 200 + 0.5 + " - " + Vd / 200 + 0.5);
    }

    public void exit() {
        AI.interrupt();
        GameMessageManager.sendMessage("EXIT");
        finish();
    }
}

class AITakesOverTheWorld extends Thread {
    private final IntelService intelService = new IntelService();
    private final double DISCHARGING = 6000;
    private long lastCharge = new Date().getTime();
    private boolean AIActivated = true;
    private boolean newInstructions = false;
    private boolean isShooting = false;
    private String color = "000000";
    private double gunTrav = 0.5;
    private double leftPower = 0.5;
    private double rightPower = 0.5;

    public void activate() {
        AIActivated = true;
    }

    public void deactivate() {
        AIActivated = false;
    }

    public void setLeftPower(double leftPower) {
        if (this.leftPower != leftPower) {
            this.leftPower = leftPower;
            newInstructions = true;
        }
    }

    public void setRightPower(double rightPower) {
        if (this.rightPower != rightPower) {
            this.rightPower = rightPower;
            newInstructions = true;
        }
    }

    public void run() {
        while (!GameMessageManager.isConnected()) {
            Log.i("AI STATUS", "READING SOME BINARY FILES ...");
            this.takeABreak(200);
        }

        Log.i("AI STATUS", "READY TO TAKE OVER THE WORLD");

        GameMessageManager.sendMessage("NAME= ");
        GameMessageManager.sendMessage("COL=000000");

        while (GameMessageManager.isConnected()) {

            if (AIActivated) {
                intelService.getIntel();

                /* Combat logic */
                targetNearestThreat();
            } else {
                color = getRandomColor();
            }

            /* Tactical logic */

            if (newInstructions) {
                sendComplexInstructions("MotL=" + leftPower + "#MotR=" + rightPower + "#GunTrig=" + (isShooting ? 1 : 0) + "#GunTrav=" + gunTrav + "#COL=" + color);
                newInstructions = false;
            } else {
                keepMeUp();
            }

            this.takeABreak(100);
        }

        Log.i("AI STATUS", "I'VE FAILED IN MY MISSION ... AUTO-DESTROYING IN PROGRESS");
    }

    @SuppressLint("DefaultLocale")
    private String getRandomColor() {
        Random obj = new Random();
        return String.format("%d%d%d%d%d%d%d%d", obj.nextInt(9), obj.nextInt(9), obj.nextInt(9), obj.nextInt(9), obj.nextInt(9), obj.nextInt(9), obj.nextInt(9), obj.nextInt(9));
    }
    private void targetNearestThreat() {
        double threatOrientation = intelService.getClosestThreatOrientation();
        double STOP = 0.5;
        if (threatOrientation == -1) {
            if (rightPower != STOP || leftPower != STOP) {
                rightPower = STOP;
                leftPower = STOP;
                isShooting = false;
                newInstructions = true;
            }
            return;
        }

        double offset = 0;

        if (false) {

            if (threatOrientation > 0.5) {
                offset = threatOrientation - 0.5;

                if (offset < 0.01) {
                    rightPower = threatOrientation;
                    //gunTrav = threatOrientation;

                } else {
                    rightPower = 1;
                    leftPower = 0;
                }

            } else {
                offset = 0.5 - threatOrientation;
                if (offset < 0.01) {
                    leftPower = threatOrientation + 0.5;
                    //gunTrav = threatOrientation;

                } else {
                    leftPower = 1;
                    rightPower = 0;
                }
            }
        } else {
            rightPower = STOP;
            leftPower = STOP;
            gunTrav = threatOrientation;
        }

        isShooting = offset < 0.1;

        if (isShooting) {
            color = "000000";
        } else {
            //color = "99999999";
        }

        Log.i("ENEMY OFFSET", threatOrientation + " # " + intelService.getPlayerOrientation() + " # " + offset);
        newInstructions = true;
    }

    private void sendComplexInstructions(String message) {
        GameMessageManager.sendMessage(message);
        iReallyLikeElectricity();
    }

    private void chargeItUp() {
        String BATTERY = "LIVE";
        sendComplexInstructions(BATTERY);

        //GameMessageManager.connect();

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
    private ArrayList<Threat> threatsList = new ArrayList<Threat>();

    public float getPlayerOrientation() {
        return playerOrientation;
    }

    public void setPlayerOrientation(float playerOrientation) {
        this.playerOrientation = playerOrientation;
    }

    public void setThreatsList(String threatsNames) {
        Log.i("setThreatsNameList", threatsNames);
        String[] threatsNameList = threatsNames.split("=");
        for (String threatName: threatsNameList) {
            Log.i("setThreatsNameList", threatName);
            threatsList.add(new Threat(threatName));
        }
    }

    public void setThreatsInfo(String threatsInfo) {
        Log.i("setThreatsInfo", threatsInfo);
        String[] threatsNameList = threatsInfo.split("=");
        for (String threatName: threatsNameList) {
            Log.i("setThreatsNameList", threatName);
            threatsList.add(new Threat(threatName));
        }
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
        StringBuilder threatInfoRequest = new StringBuilder();
        for (Threat threat: threatsList) {
            threatInfoRequest.append("#NBOT=").append(threat.name);
        }
        GameMessageManager.sendMessage("ORIENT#CBOT#CPROJ#NLIST" + threatInfoRequest);
        String encryptedInstructions = GameMessageManager.getNextMessage();

        if (encryptedInstructions == null) return;

        int index = 0;
        String[] decryptedInstructions = encryptedInstructions.split("#");
        setPlayerOrientation(Float.parseFloat(decryptedInstructions[index]));
        index++;
        setClosestThreatInfo(decryptedInstructions[index]);
        index++;
        setClosestProjectileInfo(decryptedInstructions[index]);
        index++;
        setThreatsList(decryptedInstructions[index]);
        index++;


        for (Threat threat: threatsList) {
            String threatInfo = decryptedInstructions[index];
            Log.i("threatInfo", threatInfo);
            /*
            if (Objects.equals(threatInfo, "EMPTY")) {
                threat.isAlive = false;
            } else {
                //String[] threatInfoSplit = threatInfo.split("/");
                //threat.position = Double.parseDouble(threatInfoSplit[0]);
                //threat.speed = Double.parseDouble(threatInfoSplit[1]);
                //threat.isAlive = true;
            }
            index++;

             */
        }
    }
}

class Threat {
    public String name;
    public boolean isAlive = false;
    public double speed = 0;
    public double position = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public Threat(String name) {
        this.name = name;
    }
}
