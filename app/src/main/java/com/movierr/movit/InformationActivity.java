package com.movierr.movit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Displays the information about the movie selected.
 * (Would later show availability on streaming platforms as well,
 * using GuideBox API).
 */
public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
    }
}