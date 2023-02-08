package com.example.gamecontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonControls extends AppCompatActivity {

    private Button rightButton;
    private Button leftButton;
    private Button forwardButton;
    private Button backwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rightButton = findViewById(R.id.rightButton);
        rightButton.setOnClickListener(this::turnRightEvent);

        leftButton = findViewById(R.id.startGame);
        leftButton.setOnClickListener(this::turnLeftEvent);

        forwardButton = findViewById(R.id.startGame);
        forwardButton.setOnClickListener(this::turnLeftEvent);

        backwardButton = findViewById(R.id.startGame);
        backwardButton.setOnClickListener(this::turnLeftEvent);
    }


    private void turnRightEvent(View view) {

    }

    private void turnLeftEvent(View view) {

    }

    private void leftWheel(float power) {

    }

    private void rightWheel(float power) {

    }


    public void exit(View view) {
        finish();
    }
}