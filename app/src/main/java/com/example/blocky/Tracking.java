package com.example.blocky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class Tracking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        final ConstraintLayout LAYOUT = findViewById(R.id.layout);
        final ImageView BALL_VIEW = findViewById(R.id.ballView);
        MainActivity.helpBubble(findViewById(R.id.bubbleLayout), LAYOUT,
                getString(R.string.tracking_help));
        LAYOUT.post(() -> {
            final TranslateAnimation ANIMATION = new TranslateAnimation(0,
                    LAYOUT.getWidth() - BALL_VIEW.getWidth(), 0, 0);
            ANIMATION.setDuration(3_000);
            ANIMATION.setRepeatCount(Animation.INFINITE);
            ANIMATION.setAnimationListener(new Animation.AnimationListener() {
                float y;

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    y += BALL_VIEW.getMeasuredHeight();
                    if (y >= LAYOUT.getHeight() - BALL_VIEW.getMeasuredHeight()) {
                        y = 0;
                    }
                    animation.reset();
                    BALL_VIEW.setTranslationY(y);
                }
            });
            BALL_VIEW.setAnimation(ANIMATION);
        });
    }
}