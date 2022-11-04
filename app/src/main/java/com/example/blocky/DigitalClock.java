package com.example.blocky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.blocky.db.AppDatabase;

import java.util.Random;

public class DigitalClock extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView clock, clockText, scoreView;
    private EditText answerInput;
    private int h, m, score, bestScore;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_clock);
        final ConstraintSet SET = new ConstraintSet();
        final Button NEXT_BUTTON = findViewById(R.id.nextButton),
                CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        clock = findViewById(R.id.clock);
        clockText = findViewById(R.id.clockText);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.clock);
            MainActivity.connect(SET, R.id.clock, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            clockText.setVisibility(View.GONE);
            NEXT_BUTTON.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getDigitalClock(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.clock_play));
        } else {
            MainActivity.connect(SET, R.id.clock, R.id.clockText);
            MainActivity.connect(SET, R.id.clockText, R.id.nextButton);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.clock_learn));
        }
        h = -1;
        m = -1;
        next(null);
    }

    private static String word(int x) {
        final String[] NUMBERS = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight",
                "Nine", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
                "Seventeen", "Eighteen", "Nineteen"},
                TENS = {"Ten", "Twenty", "Thirty", "Forty", "Fifty"};
        if (x == 0) {
            return "";
        } else if (x < 10) {
            return NUMBERS[x - 1];
        } else if (x > 10 && x < 20) {
            return NUMBERS[x - 2];
        } else {
            final int N = x % 10, T = x / 10;
            return TENS[T - 1] + (N > 0 ? "-" + NUMBERS[N - 1].toLowerCase() : "");
        }
    }

    private String answer() {
        String s = word(h);
        if (h == 0) {
            s = "Midnight";
        }
        return s + (m == 0 ? "" : " " + word(m).toLowerCase());
    }

    public void next(View view) {
        int x = new Random().nextInt(24);
        int y = new Random().nextInt(60);
        while (x == h && y == m) {
            x = new Random().nextInt(24);
            y = new Random().nextInt(60);
        }
        h = x;
        m = y;
        final String S = (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m);
        clock.setText(S);
        if (clockText.getVisibility() == View.VISIBLE) {
            clockText.setText(answer());
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
        next(null);
        answerInput.getText().clear();
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setDigitalClock(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}