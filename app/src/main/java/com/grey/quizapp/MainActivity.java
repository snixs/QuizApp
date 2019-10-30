package com.grey.quizapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editTextField = (EditText) findViewById(R.id.queryET);
        final Player player = new Player("name","0");



        editTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                player.setName(editTextField.getText().toString());
                Intent newPlayerIntent = new Intent(MainActivity.this, QuizActivity.class);
                newPlayerIntent.putExtra("name", player.getName());
                newPlayerIntent.addFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(newPlayerIntent);
              finish();

              return false;
            }
        });
    }


}



