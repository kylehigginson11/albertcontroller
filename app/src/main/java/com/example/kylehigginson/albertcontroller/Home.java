package com.example.kylehigginson.albertcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Home extends AppCompatActivity {

    Button avoidButton, followButton, controlButton, pictureButton, streamButton, offButton, conTestButton;
    TextView titleTV;

    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice = null;

    final byte delimiter = 33;
    int readBufferPosition = 0;

    BluetoothAdapter mBluetoothAdapter;
    InputStream mmInputStream;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        avoidButton = (Button) findViewById(R.id.avoidButton);
        followButton = (Button) findViewById(R.id.followButton);
        controlButton = (Button) findViewById(R.id.controlButton);
        pictureButton = (Button) findViewById(R.id.picButton);
        streamButton = (Button) findViewById(R.id.streamButton);
        offButton = (Button) findViewById(R.id.offButton);
        conTestButton = (Button) findViewById(R.id.conTestButton);
        titleTV = (TextView)findViewById(R.id.titleTV);

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mmDevice = device;
                    break;
                }
            }
        }

        final Handler handler = new Handler();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final class workerThread extends AsyncTask<String, Void, Void> {

            @Override
            protected Void doInBackground(String... params) {

                String btMsg = params[0];

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
                                            if (data == "connected!"){
                                                Toast.makeText(Home.this, "Alberto connected!", Toast.LENGTH_SHORT).show();
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
                try{
                    mmSocket.close();
                    mmSocket = null;
                    mmInputStream.close();
                } catch (IOException e){
                    Log.e("failed", "to close");
                }
            }
        };



        avoidButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                connectToAlbert();
                new workerThread().execute("avoid");

            }
        });



        followButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click


                connectToAlbert();
                new workerThread().execute("follow");

            }
        });


        controlButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                Intent myIntent = new Intent(Home.this, TakeControl.class);
                Home.this.startActivity(myIntent);

            }
        });


        pictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //(new Thread(new workerThread("forward"))).start();

            }
        });


        streamButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //(new Thread(new workerThread("forward"))).start();

            }
        });


        conTestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                connectToAlbert();
                new workerThread().execute("test");

            }
        });


        offButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //(new Thread(new workerThread("forward"))).start();

            }
        });

        // end light off button handler

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
    }
}
