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

public class Digits extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView digitView, scoreView;
    private EditText answerInput;
    private int digit, score, bestScore;
    private boolean flag, next, previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digits);
        final ConstraintSet SET = new ConstraintSet();
        final ConstraintLayout BUTTONS_LAYOUT = findViewById(R.id.buttonsLayout);
        final Button CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        digitView = findViewById(R.id.digitView);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.digitView);
            MainActivity.connect(SET, R.id.digitView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            BUTTONS_LAYOUT.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getDigit(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.digit_play));
            digit = -1;
            int x = new Random().nextInt(10);
            while (x == digit) {
                x = new Random().nextInt(10);
            }
            digit = x;
            if (new Random().nextBoolean()) {
                next = true;
                previous = false;
                answerInput.setHint(R.string.digit_play_next);
            } else {
                next = true;
                previous = true;
                answerInput.setHint(R.string.digit_play_previous);
            }
        } else {
            MainActivity.connect(SET, R.id.digitView, R.id.buttonsLayout);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.digit_learn));
        }
        digitView.setText(String.valueOf(digit));
    }

    private String answer() {
        int x = digit;
        if (next) {
            if (x == 9) {
                x = -1;
            }
            return "" + ++x;
        } else if (previous) {
            if (digit == 0) {
                x = 10;
            }
            return "" + --x;
        } else {
            return null;
        }
    }

    public void next(View view) {
        if (digit == 9) {
            digit = -1;
        }
        digitView.setText(String.valueOf(++digit));
    }

    public void previous(View view) {
        if (digit == 0) {
            digit = 10;
        }
        digitView.setText(String.valueOf(--digit));
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
        int x = new Random().nextInt(10);
        while (x == digit) {
            x = new Random().nextInt(10);
        }
        digit = x;
        if (new Random().nextBoolean()) {
            next = true;
            previous = false;
            answerInput.setHint(R.string.digit_play_next);
        } else {
            previous = true;
            next = false;
            answerInput.setHint(R.string.digit_play_previous);
        }
        digitView.setText(String.valueOf(digit));
        answerInput.getText().clear();
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setDigit(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}