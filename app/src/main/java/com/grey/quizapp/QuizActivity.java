package com.grey.quizapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;

import static android.widget.Toast.makeText;

public class QuizActivity extends AppCompatActivity {

    // Member variables accessible in all the methods of the MainActivity:
    Button mTrueButton;
    Button mFalseButton;
    TextView mScoreTextView;
    TextView mQuestionTextView;
    ProgressBar mProgressBar;
    int mIndex;
    int mScore;
    int mQuestion;
    String mPlayerName;
    Toast mToastMessage; // For keeping track if a Toast message is being shown.
    Toolbar mTopToolbar;

    // Create question bank using the TrueFalse class for each item in the array
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_1, true),
            new Question(R.string.question_2, true),
            new Question(R.string.question_3, true),
            new Question(R.string.question_4, true),
            new Question(R.string.question_5, true),
            new Question(R.string.question_6, false),
            new Question(R.string.question_7, true),
            new Question(R.string.question_8, false),
            new Question(R.string.question_9, true),
            new Question(R.string.question_10, true),
            new Question(R.string.question_11, false),
            new Question(R.string.question_12, false),
            new Question(R.string.question_13,true)
    };
    // Declaring constants here. Rather than a fixed number, choosing to make it a function
    // of the length of the question bank (the number of questions)
    final int PROGRESS_BAR_INCREMENT = (int) Math.ceil(100.0 / mQuestionBank.length);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar);

        // In API 26 'casting' is no longer needed.
        // For API 25 and lower: mTrueButton = (Button) findViewById(R.id.true_button);
        // For API 26 and higher can use: mTrueButton = findViewById(R.id.true_button);

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mQuestionTextView = findViewById(R.id.question_text_view);
        mScoreTextView = findViewById(R.id.score);
        mProgressBar = findViewById(R.id.progress_bar);

        Bundle bundle = getIntent().getExtras();
        mPlayerName = bundle.getString("name");

        // Restores the 'state' of the app upon screen rotation:
        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt("ScoreKey");
            mIndex = savedInstanceState.getInt("IndexKey");
            mScoreTextView.setText("Score " + mScore + "/" + mQuestionBank.length);
        } else {
            mScore = 0;
            mIndex = 0;
        }

        mQuestion = mQuestionBank[mIndex].getQuestionID();
        mQuestionTextView.setText(mQuestion);

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                updateQuestion();
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                updateQuestion();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help_friend) {
            onHelpWhatsApp();
            return true;
        }
        if (id == R.id.help_web) {
            onHelpWeb();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onHelpWhatsApp() {

        PackageManager pm = getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "Hey, can you help with this? \n" + mQuestionTextView.getText() + "\n is it True or False?";

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(waIntent);

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void onHelpWeb(){
        String q = mQuestionTextView.getText().toString();
        String url = "http://www.google.com/search?q=" + q;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Quit?");
        builder.setMessage("All data will be lost are you sure? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                QuizActivity.super.onBackPressed();
                finish();
            }
        });
        builder.setNegativeButton("Nah", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }

    private void updateQuestion() {
        // This takes the modulus. Not a division.
        mIndex = (mIndex + 1) % mQuestionBank.length;

        // Present an alert dialog if we reach the end.
        if(mIndex == 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Game Over");
            alert.setCancelable(false);
            alert.setMessage("You scored " + mScore + " points!");
            alert.setPositiveButton("Nice!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent scoresIntent = new Intent(QuizActivity.this, ScoresActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", mPlayerName);
                    bundle.putString("score", Integer.toString(mScore));
                    scoresIntent.putExtras(bundle);
                    scoresIntent.addFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(scoresIntent);
                    finish();
                }
            });
            alert.show();


        }

        mQuestion = mQuestionBank[mIndex].getQuestionID();
        mQuestionTextView.setText(mQuestion);
        mProgressBar.incrementProgressBy(PROGRESS_BAR_INCREMENT);
        mScoreTextView.setText("Score " + mScore + "/" + mQuestionBank.length);
    }

    private void checkAnswer(boolean userSelection) {

        boolean correctAnswer = mQuestionBank[mIndex].isAnswer();

        // Can cancel the Toast message if there is one on screen and a new answer
        // has been submitted.
        if (mToastMessage != null) {
            mToastMessage.cancel();
        }

        if(userSelection == correctAnswer) {
            mToastMessage = makeText(getApplicationContext(), R.string.correct_toast, Toast.LENGTH_SHORT);
            mScore = mScore + 1;

        } else {
            mToastMessage = Toast.makeText(getApplicationContext(), R.string.incorrect_toast, Toast.LENGTH_LONG);
        }

        mToastMessage.show();

    }

    // This callback is received when the screen is rotated so we can save the 'state'
    // of the app in a 'bundle'.
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putInt("ScoreKey", mScore);
        outState.putInt("IndexKey", mIndex);
    }

}



