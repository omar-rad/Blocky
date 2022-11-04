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

public class Months extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView monthView, scoreView;
    private EditText answerInput;
    private int month, score, bestScore;
    private final int[] MONTHS = {R.string.jan, R.string.feb, R.string.mar, R.string.apr,
            R.string.may, R.string.jun, R.string.jul, R.string.aug, R.string.sep, R.string.oct,
            R.string.nov, R.string.dec};
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_months);
        final ConstraintSet SET = new ConstraintSet();
        final ConstraintLayout BUTTONS_LAYOUT = findViewById(R.id.buttonsLayout);
        final Button CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        monthView = findViewById(R.id.monthView);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.monthView);
            MainActivity.connect(SET, R.id.monthView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            BUTTONS_LAYOUT.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getMonth(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.month_play));
            month = -1;
            int x = new Random().nextInt(12);
            while (x == month) {
                x = new Random().nextInt(12);
            }
            month = x;
        } else {
            MainActivity.connect(SET, R.id.monthView, R.id.buttonsLayout);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.month_learn));
        }
        monthView.setText(MONTHS[month]);
    }

    private String answer() {
        int x = month;
        if (x == 11) {
            x = -1;
        }
        return getString(MONTHS[++x]);
    }

    public void next(View view) {
        if (month == 11) {
            month = -1;
        }
        monthView.setText(MONTHS[++month]);
    }

    public void previous(View view) {
        if (month == 0) {
            month = 12;
        }
        monthView.setText(MONTHS[--month]);
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
        int x = new Random().nextInt(12);
        while (x == month) {
            x = new Random().nextInt(12);
        }
        month = x;
        monthView.setText(MONTHS[month]);
        answerInput.getText().clear();
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setMonth(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}