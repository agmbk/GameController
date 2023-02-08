package com.example.gamecontroller;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GameMessageManager {
    private static Socket soc = null;
    private static int portConnect = 6969;
    private static String servAddr = "192.168.1.98";
    private static DataOutputStream out;
    private static DataInputStream in;
    private static boolean log = false;

    public static void connect(String address) {
        setServerAddress(address);
        connect();
    }

    public static void connect(String address, int port) {
        setPort(port);
        connect(address);
    }

    public static void connect() {
        new Thread() {
            public void run() {
                try {
                    soc = new Socket(servAddr, portConnect);
                    if (soc != null) {
                        out = new DataOutputStream(soc.getOutputStream());
                        in = new DataInputStream(soc.getInputStream());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static boolean isConnected() {
        return soc != null;
    }

    public static boolean sendMessage(String msg) {
        if (isConnected()) {
            try {
                if (log)
                    Log.i("GameMessManager", "sending : " + msg);
                out.writeUTF(msg);
                return true;
            } catch (IOException e) {
                if (log)
                    Log.i("GameMessManager",
                            "attempted sendMessage got exception : "
                                    + e.getMessage());
            }
        } else {
            if (log)
                Log.i("GameMessManager",
                        "attempted sendMessage: not connected");
        }
        return false;
    }

    public static String getNextMessage() {
        if (isConnected()) {
            try {
                String msg = null;
                msg = in.readUTF();
                if (log)
                    Log.i("GameMessManager", "received : " + msg);
                return msg;
            } catch (IOException e) {
                if (log)
                    Log.i("GameMessManager",
                            "attempted getNextMessage got exception : "
                                    + e.getMessage());
            }
        } else {
            if (log)
                Log.i("GameMessManager",
                        "attempted getNextMessage: not connected");
        }
        return null;
    }

    public static void setPort(int port) {
        portConnect = port;
    }

    public static String getServerAddress() {
        return servAddr;
    }

    public static void setServerAddress(String addr) {
        servAddr = String.valueOf(addr);
    }

    public static void logActivity(boolean state) {
        log = state;
    }
}
