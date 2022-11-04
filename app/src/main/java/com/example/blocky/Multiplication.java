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

public class Multiplication extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView multiplicationView, multiplicationText, scoreView;
    private EditText answerInput;
    private int m, n, score, bestScore;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplication);
        final ConstraintSet SET = new ConstraintSet();
        final Button NEXT_BUTTON = findViewById(R.id.nextButton),
                CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        multiplicationView = findViewById(R.id.multiplicationView);
        multiplicationText = findViewById(R.id.multiplicationText);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.multiplicationView);
            MainActivity.connect(SET, R.id.multiplicationView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            multiplicationText.setVisibility(View.GONE);
            NEXT_BUTTON.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getMultiply(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.multiply_play));
        } else {
            MainActivity.connect(SET, R.id.multiplicationView, R.id.multiplicationText);
            MainActivity.connect(SET, R.id.multiplicationText, R.id.nextButton);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.multiply_learn));
        }
        m = -1;
        n = -1;
        next(null);
    }

    private String answer() {
        return "" + m * n;
    }

    public void next(View view) {
        int x = new Random().nextInt(10) + 1;
        int y = new Random().nextInt(10) + 1;
        while (x == m && y == n) {
            x = new Random().nextInt(10) + 1;
            y = new Random().nextInt(10) + 1;
        }
        m = x;
        n = y;
        final String S = m + " X " + n;
        multiplicationView.setText(S);
        if (multiplicationText.getVisibility() == View.VISIBLE) {
            multiplicationText.setText(answer());
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
                    setMultiply(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}