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
import com.iigo.library.ClockView;

import java.util.Random;

public class AnalogClock extends AppCompatActivity {
    private ConstraintLayout layout;
    private ClockView clockView;
    private TextView clockText, scoreView;
    private EditText answerInput;
    private int h, m, score, bestScore;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analog_clock);
        final ConstraintSet SET = new ConstraintSet();
        final Button NEXT_BUTTON = findViewById(R.id.nextButton),
                CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        clockView = findViewById(R.id.clockView);
        clockText = findViewById(R.id.clockText);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.clockView);
            MainActivity.connect(SET, R.id.clockView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            clockText.setVisibility(View.GONE);
            NEXT_BUTTON.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getAnalogClock(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
            MainActivity.connect(SET, R.id.clockView, R.id.clockText);
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

    private String answer() {
        if (h == 0) {
            h = 12;
        }
        if (m == 0 || m == 60) {
            return h + " O'clock";
        } else if (m == 15) {
            return "Quarter past " + h;
        } else if (m == 30) {
            return "Half past " + h;
        } else if (m == 45) {
            return "Quarter to " + (h + 1 > 12 ? 1 : h + 1);
        } else if (m < 30) {
            return m + " past " + h;
        } else {
            return (60 - m) + " to " + (h + 1 > 12 ? 1 : h + 1);
        }
    }

    public void next(View view) {
        int x = new Random().nextInt(13);
        int y = new Random().nextInt(61);
        while (x == h && y == m) {
            x = new Random().nextInt(13);
            y = new Random().nextInt(61);
        }
        h = x;
        m = y;
        clockView.setTime(h, m, 0);
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
                    setAnalogClock(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}