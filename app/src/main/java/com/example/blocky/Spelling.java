package com.example.blocky;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.blocky.db.AppDatabase;

import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class Spelling extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView wordView, scoreView;
    private int word, score, bestScore;
    private final HashSet<String> WORDS = new HashSet<>(100);
    private boolean flag, play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spelling);
        final ConstraintSet SET = new ConstraintSet();
        final ConstraintLayout BUTTONS_LAYOUT = findViewById(R.id.buttonsLayout);
        final Button NEXT_BUTTON = findViewById(R.id.nextButton);
        layout = findViewById(R.id.layout);
        SET.clone(layout);
        wordView = findViewById(R.id.wordView);
        scoreView = findViewById(R.id.scoreView);
        if (getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            MainActivity.connect(SET, R.id.scoreView, R.id.wordView);
            MainActivity.connect(SET, R.id.wordView, R.id.buttonsLayout);
            SET.applyTo(layout);
            NEXT_BUTTON.setVisibility(View.GONE);
            final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                            getApplicationContext()).userDao()
                    .getWord(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
                    getString(R.string.word_play));
            play = true;
        } else {
            MainActivity.connect(SET, R.id.wordView, R.id.nextButton);
            SET.applyTo(layout);
            scoreView.setVisibility(View.GONE);
            BUTTONS_LAYOUT.setVisibility(View.GONE);
            MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                    getString(R.string.word_learn));
        }
        try (final Scanner SCANNER = new Scanner(getResources().openRawResource(R.raw.words))) {
            while (SCANNER.hasNextLine()) {
                WORDS.add(SCANNER.nextLine());
            }
        }
        word = -1;
        next(null);
    }

    public void next(View view) {
        String[] words = new String[100];
        WORDS.toArray(words);
        int x = new Random().nextInt(100);
        while (x == word) {
            x = new Random().nextInt(100);
        }
        word = x;
        String wordS = words[word];
        if (play) {
            final Button OPTION_BUTTON = findViewById(R.id.optionButton);
            final Button OPTION_BUTTON2 = findViewById(R.id.optionButton2);
            final char[] W_CHARS = wordS.toCharArray();
            final int INDEX = new Random().nextInt(W_CHARS.length);
            final char CORRECT = W_CHARS[INDEX];
            char wrong = (char) (new Random().nextInt(26) + 'a');
            while (wrong == CORRECT) {
                wrong = (char) (new Random().nextInt(26) + 'a');
            }
            W_CHARS[INDEX] = '_';
            wordS = new String(W_CHARS);
            final String CORRECT_S = String.valueOf(CORRECT), WRONG_S = String.valueOf(wrong);
            if (new Random().nextBoolean()) {
                OPTION_BUTTON.setText(CORRECT_S);
                OPTION_BUTTON2.setText(WRONG_S);
                OPTION_BUTTON.setOnClickListener(this::onCorrect);
                OPTION_BUTTON2.setOnClickListener(this::onWrong);
            } else {
                OPTION_BUTTON.setText(WRONG_S);
                OPTION_BUTTON2.setText(CORRECT_S);
                OPTION_BUTTON.setOnClickListener(this::onWrong);
                OPTION_BUTTON2.setOnClickListener(this::onCorrect);
            }
        }
        final String S = wordS.substring(0, 1).toUpperCase() + wordS.substring(1).toLowerCase();
        wordView.setText(S);
    }

    public void onCorrect(View view) {
        score++;
        if (score > bestScore) {
            flag = true;
            bestScore = score;
            final String S = getString(R.string.best_score) + ": " + bestScore;
            scoreView.setText(S);
        }
        MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                getString(R.string.correct));
        next(null);
    }

    public void onWrong(View view) {
        score = 0;
        MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                getString(R.string.wrong));
        next(null);
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setWord(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}