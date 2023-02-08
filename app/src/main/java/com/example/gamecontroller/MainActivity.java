package com.example.gamecontroller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final String IP_KEY = "USERNAME_KEY";
    private EditText ipText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startGameButton = findViewById(R.id.startGame);
        startGameButton.setOnClickListener(this::startGame);

        ipText = findViewById(R.id.ipAddress);

        /*
        IA TAKE OVER THE WORLD
         */
        String ip = ipText.getText().toString();
        GameMessageManager.setServerAddress(ip);
        GameMessageManager.connect();
        Intent intent = new Intent(this, KickThatDriverOut.class);
        findViewById(R.id.ipError).setVisibility(View.INVISIBLE);
        startActivity(intent);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        ipText.setText(savedInstanceState.getString(IP_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(IP_KEY, ipText.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void startGame(View view) {

        String ip = ipText.getText().toString();

        GameMessageManager.setServerAddress(ip);

        GameMessageManager.connect();

        Intent intent = new Intent(this, KickThatDriverOut.class);

        findViewById(R.id.ipError).setVisibility(View.INVISIBLE);

        startActivity(intent);
    }

    public void exit(View view) {
        finish();
    }
}