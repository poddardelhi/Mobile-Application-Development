package com.example.nishant.homeworkii;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Nishant on 15-Sep-16.
 */
public class GreetingsActvity extends Activity {

    Button b2;
    EditText textTo;
    EditText textSubject;
    EditText textMessage;
    TextView T3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greetingsactivity);
        Log.e("fadmsfjhdfn",getIntent().getExtras().getString("hello"));
         b2 = (Button) findViewById(R.id.button2);
        textTo = (EditText) findViewById(R.id.editText2);
        textSubject = (EditText) findViewById(R.id.editText3);
        textMessage = (EditText) findViewById(R.id.editText4);
        T3 = (TextView) findViewById(R.id.textView3);

        T3.setText(getIntent().getExtras().getString("hello"));

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = textTo.getText().toString();
                String subject = textSubject.getText().toString();
                String message = textMessage.getText().toString();

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});

                email.putExtra(Intent.EXTRA_SUBJECT, subject);
                email.putExtra(Intent.EXTRA_TEXT, message);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
finish();
            }
        });
    }
}
