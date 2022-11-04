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

public class Days extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView dayView, scoreView;
    private EditText answerInput;
    private int day, score, bestScore;
    private final int[] DAYS = {R.string.mon, R.string.tue, R.string.wed, R.string.thu,
            R.string.fri, R.string.sat, R.string.sun};
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);
        final ConstraintSet SET = new ConstraintSet();
        final ConstraintLayout BUTTONS_LAYOUT = findViewById(R.id.buttonsLayout);
        final Button CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        dayView = findViewById(R.id.dayView);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.dayView);
            MainActivity.connect(SET, R.id.dayView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            BUTTONS_LAYOUT.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getDay(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.day_play));
            day = -1;
            int x = new Random().nextInt(7);
            while (x == day) {
                x = new Random().nextInt(7);
            }
            day = x;
        } else {
            MainActivity.connect(SET, R.id.dayView, R.id.buttonsLayout);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.day_learn));
        }
        dayView.setText(DAYS[day]);
    }

    private String answer() {
        int x = day;
        if (x == 6) {
            x = -1;
        }
        return getString(DAYS[++x]);
    }

    public void next(View view) {
        if (day == 6) {
            day = -1;
        }
        dayView.setText(DAYS[++day]);
    }

    public void previous(View view) {
        if (day == 0) {
            day = 7;
        }
        dayView.setText(DAYS[--day]);
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
        int x = new Random().nextInt(7);
        while (x == day) {
            x = new Random().nextInt(7);
        }
        day = x;
        dayView.setText(DAYS[day]);
        answerInput.getText().clear();
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setDay(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}