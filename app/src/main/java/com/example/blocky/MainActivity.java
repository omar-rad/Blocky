package com.example.blocky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocky.db.AppDatabase;
import com.example.blocky.db.User;
import com.example.blocky.db.UserDao;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myapplication.MESSAGE";
    private EditText usernameInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
    }

    public void signIn(View view) {
        boolean flag = true;
        final String USERNAME = usernameInput.getText().toString().trim();
        final String PASSWORD = passwordInput.getText().toString();
        final Drawable ICON = AppCompatResources.getDrawable(getApplicationContext(),
                R.drawable.ic_input_error);
        assert ICON != null;
        ICON.setBounds(0, 0, ICON.getIntrinsicWidth(), ICON.getIntrinsicHeight());
        if (USERNAME.isEmpty()) {
            usernameInput.setError(getText(R.string.username_err), ICON);
            flag = false;
        }
        if (PASSWORD.length() < 8) {
            passwordInput.setError(getText(R.string.password_err), ICON);
            flag = false;
        }
        if (flag) {
            final User USER = new User(USERNAME, PASSWORD);
            new Thread(() -> {
                UserDao userDao = AppDatabase.getDatabase(getApplicationContext()).userDao();
                try {
                    userDao.insert(USER);
                } catch (SQLiteConstraintException e) {
                    if (PASSWORD.equals(userDao.getPassword(USERNAME))) {
                        final Intent INTENT = new Intent(this, Home.class);
                        INTENT.putExtra(EXTRA_MESSAGE, USERNAME);
                        startActivity(INTENT);
                    } else {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                                getText(R.string.incorrect_password), Toast.LENGTH_SHORT).show());
                    }
                }
            }).start();
        }
    }

    static void connect(ConstraintSet set, int top, int bottom) {
        set.connect(top, ConstraintSet.BOTTOM, bottom, ConstraintSet.TOP);
        set.connect(bottom, ConstraintSet.TOP, top, ConstraintSet.BOTTOM);
    }

    private static void helpBubble(RelativeLayout bubble, ViewGroup parent, String text,
                                   boolean show) {
        if (text.length() > 92) {
            throw new IllegalArgumentException();
        }
        ((TextView) bubble.findViewById(R.id.bubbleText)).setText(text);
        final Transition T = new Slide(Gravity.BOTTOM);
        T.setDuration(500);
        T.addTarget(bubble);
        TransitionManager.beginDelayedTransition(parent, T);
        bubble.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        if (show) {
            final Handler H = new Handler(Looper.getMainLooper());
            H.postDelayed(() -> helpBubble(bubble, parent, text, false),
                    text.length() * 100);
        }
    }

    static void helpBubble(RelativeLayout bubble, ViewGroup parent, String text) {
        parent.post(() -> helpBubble(bubble, parent, text, true));
    }
}