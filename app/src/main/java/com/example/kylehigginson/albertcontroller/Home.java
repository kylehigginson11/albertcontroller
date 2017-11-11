package com.example.kylehigginson.albertcontroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class Home extends AppCompatActivity {

    Button avoidButton, followButton, controlButton, pictureButton, viewPicButton, offButton, conTestButton;
    TextView titleTV;
    int SCREEN_HEIGHT, SCREEN_WIDTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        avoidButton = (Button) findViewById(R.id.avoidButton);
        followButton = (Button) findViewById(R.id.followButton);
        controlButton = (Button) findViewById(R.id.controlButton);
        pictureButton = (Button) findViewById(R.id.picButton);
        viewPicButton = (Button) findViewById(R.id.viewPicButton);
        offButton = (Button) findViewById(R.id.offButton);
        conTestButton = (Button) findViewById(R.id.conTestButton);
        titleTV = (TextView)findViewById(R.id.titleTV);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        SCREEN_WIDTH = displayMetrics.widthPixels;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }



        avoidButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(Home.this).execute("avoid", "0");

            }
        });



        followButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                CharSequence colors[] = new CharSequence[] {"Red", "Blue", "Green"};

                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setTitle("Pick a Color to Follow");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            new MessageAlbert(Home.this).execute("follow_red", "0");
                        } else if (which == 1){
                            new MessageAlbert(Home.this).execute("follow_blue", "0");
                        } else if (which == 2){
                            new MessageAlbert(Home.this).execute("follow_green", "0");
                        }
                    }
                });
                builder.show();

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

                new MessageAlbert(Home.this).execute("picture", "1");

            }
        });


        viewPicButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ImageView imageView = new ImageView(Home.this);
                SharedPreferences sharedPref = Home.this.getSharedPreferences("com.example.kylehigginson.albertcontroller", Context.MODE_PRIVATE);
                String publicID = sharedPref.getString("pictureURL", "defaultValue");
                String imageURL = String.format("http://res.cloudinary.com/dtumd2ht6/image/upload/w_%s/%s%s.jpg", SCREEN_WIDTH, publicID, "%21");
                Log.d("imagURL", imageURL);
                Picasso.with(Home.this)
                        .load(imageURL)
                        .placeholder(R.drawable.loading_animation)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Dialog builder = new Dialog(Home.this);
                                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                builder.getWindow().setBackgroundDrawable(
                                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        Log.d("Dismissed:", "True");
                                    }
                                });

                                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
                                builder.show();
                            }
                            @Override
                            public void onError() {
                                Toast.makeText(Home.this, "Could not load progress pic for session", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        conTestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(Home.this).execute("test", "0");

            }
        });


        offButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //(new Thread(new workerThread("forward"))).start();

            }
        });

    }

}
