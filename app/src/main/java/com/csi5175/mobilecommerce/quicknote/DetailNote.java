package com.csi5175.mobilecommerce.quicknote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Administrator on 1/17/2018.
 */

public class DetailNote extends AppCompatActivity {

    TextView title;
    TextView time;
    TextView context;

    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_detail);

        title = (TextView)findViewById(R.id.textView4);
        time = (TextView)findViewById(R.id.textView6);
        context = (TextView)findViewById(R.id.textView10);

        Intent i = getIntent();
        String titlec = i.getStringExtra("title");
        String timec = i.getStringExtra("time");
        String contextc = i.getStringExtra("context");
        //for the picture
        String pathc = i.getStringExtra("path");

        title.setText(titlec);
        time.setText(timec);
        context.setText(contextc);

        back=(Button)findViewById(R.id.button7);
        back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

    }
}
