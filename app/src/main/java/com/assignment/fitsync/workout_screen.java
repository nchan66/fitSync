package com.assignment.fitsync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class workout_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_screen);
    }

    public void tryAdd(View view) {

        //intent to start other activity
        Intent add_exer = new Intent(this, add_exercise.class);
        startActivity(add_exer);


    }
}
