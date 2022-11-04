package com.example.blocky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blocky.db.AppDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class Pictures extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView scoreView;
    private final ImageView[] VIEWERS = new ImageView[6];
    private final int[] PICTURES = new int[16];
    private final HashMap<Integer, Integer> PAIRS = new HashMap<>(6);
    private final HashSet<Integer> DONE = new HashSet<>(6);
    private int selected, count, score, bestScore;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        layout = findViewById(R.id.layout);
        scoreView = findViewById(R.id.scoreView);
        for (int i = 0; i < 6; i++) {
            VIEWERS[i] = findViewById(getResources().getIdentifier("imageView" + (i + 1),
                    "id", getPackageName()));
        }
        for (int i = 0; i < 16; i++) {
            PICTURES[i] = getResources().getIdentifier("avatar_" + (i + 1),
                    "drawable", getPackageName());
        }
        final Thread T = new Thread(() -> bestScore = AppDatabase.getDatabase(
                        getApplicationContext()).userDao()
                .getPicture(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE)));
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
        initialize();
        MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                getString(R.string.picture));
    }

    private void initialize() {
        HashSet<Integer> set = new HashSet<>(3);
        int i = 0, x = new Random().nextInt(16);
        while (i < 3) {
            if (set.contains(x)) {
                x = new Random().nextInt(16);
            } else {
                set.add(x);
                i++;
            }
        }
        final Iterator<Integer> ITERATOR = set.iterator();
        set = new HashSet<>(3);
        x = new Random().nextInt(6);
        int y = new Random().nextInt(6);
        for (ImageView v :
                VIEWERS) {
            v.setBackgroundTintList(AppCompatResources.getColorStateList(
                    getApplicationContext(), R.color.primary));
        }
        while (ITERATOR.hasNext()) {
            if (set.contains(x)) {
                x = new Random().nextInt(6);
            } else if (set.contains(y) || x == y) {
                y = new Random().nextInt(6);
            } else {
                set.add(x);
                set.add(y);
                final int PIC_ID = PICTURES[ITERATOR.next()], VX = VIEWERS[x].getId(),
                        VY = VIEWERS[y].getId();
                PAIRS.put(VX, VY);
                PAIRS.put(VY, VX);
                VIEWERS[x].setBackgroundResource(PIC_ID);
                VIEWERS[y].setBackgroundResource(PIC_ID);
            }
        }
        final Handler H = new Handler(Looper.getMainLooper());
        H.postDelayed(() -> {
            for (ImageView v :
                    VIEWERS) {
                v.setBackgroundTintList(null);
            }
            H.postDelayed(() -> {
                for (ImageView v :
                        VIEWERS) {
                    v.setBackgroundTintList(AppCompatResources.getColorStateList(
                            getApplicationContext(), R.color.primary));
                    v.setOnClickListener(this::onClick);
                }
            }, 3_000);
        }, 3_000);
    }

    public void onClick(View view) {
        if (selected == 0) {
            view.setBackgroundTintList(null);
            selected = view.getId();
        } else if (selected != view.getId()) {
            view.setBackgroundTintList(null);
            for (ImageView v :
                    VIEWERS) {
                v.setClickable(false);
            }
            if (Objects.equals(PAIRS.get(selected), view.getId())) {
                score++;
                count++;
                if (score > bestScore) {
                    flag = true;
                    bestScore = score;
                    final String S = getString(R.string.best_score) + ": " + bestScore;
                    scoreView.setText(S);
                }
                MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                        getString(R.string.correct));
                DONE.add(selected);
                DONE.add(view.getId());
                if (count == 3) {
                    count = 0;
                    DONE.clear();
                    final Handler H = new Handler(Looper.getMainLooper());
                    H.postDelayed(this::initialize, 3_000);
                } else {
                    for (ImageView v :
                            VIEWERS) {
                        if (!DONE.contains(v.getId())) {
                            v.setClickable(true);
                        }
                    }
                }
            } else {
                score = 0;
                MainActivity.helpBubble(findViewById(R.id.bubbleLayout), layout,
                        getString(R.string.wrong));
                final Handler H = new Handler(Looper.getMainLooper());
                H.postDelayed(() -> {
                    for (ImageView v :
                            VIEWERS) {
                        if (!DONE.contains(v.getId())) {
                            v.setBackgroundTintList(AppCompatResources.getColorStateList(
                                    getApplicationContext(), R.color.primary));
                            v.setClickable(true);
                        }
                    }
                }, 1_500);
            }
            selected = 0;
        }
    }

    @Override
    public void onBackPressed() {
        if (flag && getIntent().getBooleanExtra(Home.EXTRA_BOOLEAN, false)) {
            new Thread(() -> AppDatabase.getDatabase(getApplicationContext()).userDao().
                    setPicture(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE),
                            bestScore)).start();
        }
        super.onBackPressed();
    }
}