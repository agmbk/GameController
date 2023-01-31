package com.example.gamecontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText ipText;
    private final String IP_KEY = "USERNAME_KEY";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startGameButton = findViewById(R.id.startGame);
        startGameButton.setOnClickListener(this::startGame);

        ipText = findViewById(R.id.ipAddress);
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

        System.out.println(ip);

        GameMessageManager.setServerAddress(ip);
        System.out.println(GameMessageManager.getServerAddress());

        GameMessageManager.connect();

        System.out.println(GameMessageManager.isConnected());

        Intent intent = new Intent(this, GameButtonControls.class);

        findViewById(R.id.ipError).setVisibility(View.INVISIBLE);

        startActivity(intent);


        /*if (ip.length() == 0) {
            findViewById(R.id.ipError).setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(this, JoystickView.class);
            findViewById(R.id.ipError).setVisibility(View.INVISIBLE);
            startActivity(intent);
        }
         */
    }

    public void exit(View view) {
        finish();
    }
}