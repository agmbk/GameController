package com.example.gamecontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class GameButtonControls extends AppCompatActivity {

    SendMessage sendMessage = new SendMessage();
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
                        sendMessage.setRight(1);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        sendMessage.setRight(0);
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
                        sendMessage.setLeft(1);
                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:
                        sendMessage.setLeft(0);
                        break;
                }
                return true;
            }
        });

        forwardButton = findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(this::turnLeftEvent);

        backwardButton = findViewById(R.id.backwardButton);
        backwardButton.setOnClickListener(this::turnLeftEvent);

        gameExitButton = findViewById(R.id.gameExit);
        gameExitButton.setOnClickListener(this::exit);

        sendMessage.start();
    }

    private void turnRightEvent(View view) {
        rightWheel(1);
    }

    private void turnLeftEvent(View view) {
    leftWheel(1);
    }

    private void leftWheel(double power) {
        GameMessageManager.sendMessage("MotL=" + power);
        //System.out.println(GameMessageManager.getNextMessage());
    }

    private void rightWheel(double power) {
        GameMessageManager.sendMessage("MotR=" + power);
        //System.out.println(GameMessageManager.getNextMessage());
    }

    public void exit(View view) {
        GameMessageManager.sendMessage("EXIT");
        finish();
    }
}

class SendMessage extends Thread {

    private double left = 0;
    private double right = 0;

    public void run() {
        if (left !=0 && right!=0) {
            GameMessageManager.sendMessage("MotR=" + right + "#MotL=" + left);
        }
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public void setRight(double right) {
        this.right = right;
    }
}
