package com.example.blocky;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.blocky.db.AppDatabase;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


public class Seasons extends AppCompatActivity {

    private ConstraintLayout layout;
    private GifImageView seasonView;
    private TextView seasonText, scoreView;
    private EditText answerInput;
    private int season, score, bestScore;
    private final int[] SEASONS = {R.drawable.winter, R.drawable.spring,
            R.drawable.summer, R.drawable.autumn};
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);
        final ConstraintSet SET = new ConstraintSet();
        final ConstraintLayout BUTTONS_LAYOUT = findViewById(R.id.buttonsLayout);
        final Button CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        seasonView = findViewById(R.id.seasonView);
        seasonText = findViewById(R.id.seasonText);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.seasonView);
            MainActivity.connect(SET, R.id.seasonView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            seasonText.setVisibility(View.GONE);
            BUTTONS_LAYOUT.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getSeason(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
            T.start();
            try {
                T.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (bestScore != 0) {
                final String S = getString(R.string.best_score) + ": " + bestScore;
                scoreView.setText(S);
            }
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.season_play));
            season = -1;
            int x = new Random().nextInt(4);
            while (x == season) {
                x = new Random().nextInt(4);
            }
            season = x;
        } else {
            MainActivity.connect(SET, R.id.seasonView, R.id.seasonText);
            MainActivity.connect(SET, R.id.seasonText, R.id.buttonsLayout);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            seasonText.setText(answer());
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.season_learn));
        }
        seasonView.setImageResource(SEASONS[season]);
    }

    private String answer() {
        if (season == 0) {
            return getString(R.string.winter);
        } else if (season == 1) {
            return getString(R.string.spring);
        } else if (season == 2) {
            return getString(R.string.summer);
        } else {
            return getString(R.string.autumn);
        }
    }

    public void next(View view) {
        if (season == 3) {
            season = -1;
        }
        seasonView.setImageResource(SEASONS[++season]);
        if (seasonText.getVisibility() == View.VISIBLE) {
            seasonText.setText(answer());
        }
    }

    public void previous(View view) {
        if (season == 0) {
            season = 4;
        }
        seasonView.setImageResource(SEASONS[--season]);
        if (seasonText.getVisibility() == View.VISIBLE) {
            seasonText.setText(answer());
        }
    }

    public void check(View view) {
        if (answerInput.getText().toString().trim().equalsIgnoreCase(answer())) {
            score++;
            if (score > bestScore) {
                flag = true;
                bestScore = score;
                final String S = getString(R.string.best_score) + ": " + bestScore;
                scoreView.setText(S);
            }
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.correct));
        } else {
            score = 0;
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.wrong));
        }
        int x = new Random().nextInt(4);
        while (x == season) {
            x = new Random().nextInt(4);
        }
        season = x;
        seasonView.setImageResource(SEASONS[season]);
        answerInput.getText().clear();
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setSeason(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}