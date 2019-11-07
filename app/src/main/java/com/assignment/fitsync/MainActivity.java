package com.assignment.fitsync;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void tryLogin(View view) {

        //intent to start other activity
        Intent intent = new Intent(this, workout_screen.class);
        startActivity(intent);


    }
}
