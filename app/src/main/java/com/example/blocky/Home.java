package com.example.blocky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class Home extends AppCompatActivity {
    public static final String EXTRA_BOOLEAN = "com.example.myapplication.BOOLEAN";
    private SwitchMaterial modeSwitch;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        username = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        setTitle(getText(R.string.welcome) + ", " + username);
        final Button PICTURES_BUTTON = findViewById(R.id.picturesButton),
                TRACKING_BUTTON = findViewById(R.id.trackingButton);
        PICTURES_BUTTON.setVisibility(View.GONE);
        modeSwitch = findViewById(R.id.modeSwitch);
        modeSwitch.setOnCheckedChangeListener((CompoundButton view, boolean isCheck) -> {
            if (isCheck) {
                modeSwitch.setText(R.string.play);
                PICTURES_BUTTON.setVisibility(View.VISIBLE);
                TRACKING_BUTTON.setVisibility(View.GONE);
            } else {
                modeSwitch.setText(R.string.learn);
                PICTURES_BUTTON.setVisibility(View.GONE);
                TRACKING_BUTTON.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startActivity(Class<?> cls) {
        final Intent INTENT = new Intent(this, cls);
        INTENT.putExtra(MainActivity.EXTRA_MESSAGE, username);
        INTENT.putExtra(EXTRA_BOOLEAN, modeSwitch.isChecked());
        startActivity(INTENT);
    }

    public void analogClock(View view) {
        startActivity(AnalogClock.class);
    }

    public void digitalClock(View view) {
        startActivity(DigitalClock.class);
    }

    public void seasons(View view) {
        startActivity(Seasons.class);
    }

    public void days(View view) {
        startActivity(Days.class);
    }

    public void months(View view) {
        startActivity(Months.class);
    }

    public void digits(View view) {
        startActivity(Digits.class);
    }

    public void spelling(View view) {
        startActivity(Spelling.class);
    }

    public void directions(View view) {
        startActivity(Directions.class);
    }

    public void multiplication(View view) {
        startActivity(Multiplication.class);
    }

    public void pictures(View view) {
        final Intent INTENT = new Intent(this, Pictures.class);
        INTENT.putExtra(MainActivity.EXTRA_MESSAGE, username);
        startActivity(INTENT);
    }

    public void tracking(View view) {
        final Intent INTENT = new Intent(this, Tracking.class);
        startActivity(INTENT);
    }
}