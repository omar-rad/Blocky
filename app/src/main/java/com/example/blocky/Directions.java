package com.example.blocky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blocky.db.AppDatabase;

import java.util.Random;

public class Directions extends AppCompatActivity {

    private ConstraintLayout layout;
    private ImageView directionView;
    private TextView directionText, scoreView;
    private EditText answerInput;
    private int direction, score, bestScore;
    private final int[] DIRECTIONS = {R.drawable.left, R.drawable.up, R.drawable.right,
            R.drawable.down};
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
        final ConstraintSet SET = new ConstraintSet();
        final Button NEXT_BUTTON = findViewById(R.id.nextButton),
                CHECK_BUTTON = findViewById(R.id.checkButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        directionView = findViewById(R.id.directionView);
        directionText = findViewById(R.id.directionText);
        scoreView = findViewById(R.id.scoreView);
        answerInput = findViewById(R.id.answerInput);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.directionView);
            MainActivity.connect(SET, R.id.directionView, R.id.answerInput);
            MainActivity.connect(SET, R.id.answerInput, R.id.checkButton);
            SET.applyTo(layout);
            directionText.setVisibility(View.GONE);
            NEXT_BUTTON.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getDirection(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.direction_play));
        } else {
            MainActivity.connect(SET, R.id.directionView, R.id.directionText);
            MainActivity.connect(SET, R.id.directionText, R.id.nextButton);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            answerInput.setVisibility(View.GONE);
            CHECK_BUTTON.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.direction_learn));
        }
        direction = -1;
        next(null);
    }

    private String answer() {
        if (direction == 0) {
            return "Left";
        } else if (direction == 1) {
            return "Up";
        } else if (direction == 2) {
            return "Right";
        } else {
            return "Down";
        }
    }

    public void next(View view) {
        int x = new Random().nextInt(4);
        while (x == direction) {
            x = new Random().nextInt(4);
        }
        direction = x;
        directionView.setImageResource(DIRECTIONS[direction]);
        if (directionText.getVisibility() == View.VISIBLE) {
            directionText.setText(answer());
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
                    setDirection(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}