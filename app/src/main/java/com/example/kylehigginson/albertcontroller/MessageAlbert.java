package com.example.kylehigginson.albertcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by kylehigginson on 31/08/2017.
 */

public class MessageAlbert extends AsyncTask<String, Void, Void> {

    private Context mContext;

    private Boolean cancelled = false;

    public MessageAlbert(Context context){
        mContext = context;
    }

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;

    BluetoothAdapter mBluetoothAdapter;
    InputStream mmInputStream;

    final Handler handler = new Handler();

    public void connectToAlbert(){

        Log.d("Connecting to Albert", "now");

        UUID uuid = UUID.fromString("5c01c1ce-fe60-428a-8e68-0be0e8ed6b7a"); //Standard SerialPortService ID

        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            Log.d("socket created", "now");
        } catch (Exception e) {
            Log.e("","Error creating socket");
        }

        try {
            mmSocket.connect();
            Log.d("","Connected");
        } catch (IOException e) {
            Log.e("",e.getMessage());
            try {
                Log.e("","trying fallback...");

                mmSocket =(BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mmDevice,1);
                mmSocket.connect();

                Log.e("","Connected");
            }
            catch (Exception e2) {
                Log.e("", "Couldn't establish Bluetooth connection!");
            }
        }
    }

    public void sendBtMsg(String msg2send) {

        try {
            String msg = msg2send;
            //msg += "\n";
            OutputStream mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(msg.getBytes());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute(){

        Boolean gotAlbert = false;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mmDevice = device;
                    gotAlbert = true;
                    break;
                }
            }
        }
        if (!gotAlbert){
            cancelled = true;
            Toast.makeText(mContext, "Cant Connect to Albert", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected Void doInBackground(String... params) {

        if (cancelled){
            Log.d("Cancelled:", "True");
            return null;
        }
        connectToAlbert();

        String btMsg = params[0];
        final String picture = params[1];

        sendBtMsg(btMsg);
        while (!Thread.currentThread().isInterrupted()) {
            int bytesAvailable;
            boolean workDone = false;

            try {

                mmInputStream = mmSocket.getInputStream();
                bytesAvailable = mmInputStream.available();
                if (bytesAvailable > 0) {

                    byte[] packetBytes = new byte[bytesAvailable];
                    Log.e("Aquarium recv bt", "bytes available");
                    byte[] readBuffer = new byte[1024];
                    mmInputStream.read(packetBytes);

                    for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        if (b == delimiter) {
                            byte[] encodedBytes = new byte[readBufferPosition];
                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                            final String data = new String(encodedBytes, "US-ASCII");
                            readBufferPosition = 0;

                            //The variable data now contains our full command
                            handler.post(new Runnable() {
                                public void run() {
                                    if (data.equals("connected!")){
                                        Toast.makeText(mContext, "Alberto connected!", Toast.LENGTH_SHORT).show();
                                    }
                                    if (picture.equals("1")){
                                        SharedPreferences prefs = mContext.getSharedPreferences(
                                                "com.example.kylehigginson.albertcontroller", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("pictureURL", data);
                                        editor.apply();
                                    }
                                    Log.d("Data:", data);
                                }
                            });

                            workDone = true;
                            break;


                        } else {
                            readBuffer[readBufferPosition++] = b;
                        }
                    }

                    if (workDone) {
                        break;
                    }

                }
            } catch (IOException | NullPointerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (cancelled){
            return;
        }
        try{
            mmSocket.close();
            mmSocket = null;
            mmInputStream.close();
        } catch (IOException e){
            Log.e("failed", "to close");
        }
    }
};
