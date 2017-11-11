package com.example.kylehigginson.albertcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TakeControl extends AppCompatActivity {

    Button upButton, downButton, leftButton, rightButton, lookLeftButton, lookRightButton, lookCenterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_control);

        upButton = (Button)findViewById(R.id.upButton);
        downButton = (Button)findViewById(R.id.downButton);
        leftButton = (Button)findViewById(R.id.leftButton);
        rightButton = (Button)findViewById(R.id.rightButton);
        lookLeftButton = (Button)findViewById(R.id.lookLeftButton);
        lookRightButton = (Button)findViewById(R.id.lookRightButton);
        lookCenterButton = (Button)findViewById(R.id.lookCenterButton);

        upButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("up", "0");

            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("down", "0");

            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("left", "0");

            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("right", "0");

            }
        });

        lookLeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("look_left", "0");

            }
        });

        lookRightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("look_right", "0");

            }
        });

        lookCenterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on temp button click

                new MessageAlbert(TakeControl.this).execute("look_center", "0");

            }
        });

    }
}
