package com.example.nishant.homeworkii;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
   EditText E1;
    String value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1= (Button)findViewById(R.id.button);
    E1 = (EditText) findViewById(R.id.editText);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value=E1.getText().toString();
                Intent intent=new Intent (MainActivity.this,GreetingsActvity.class);
                intent.putExtra("hello",value);
                Log.e("MMMMMMMMMMMMMMMMMM",value);
                startActivity(intent);

            }
        });
    }
}
