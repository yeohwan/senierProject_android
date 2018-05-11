package com.example.cwss1.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class detailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView tvID = (TextView)findViewById(R.id.textView1);
        TextView tvNAME = (TextView)findViewById(R.id.textView2);

        Intent intent = getIntent(); // 보내온 Intent를 얻는다

        tvID.setText(intent.getStringExtra("id"));
        tvNAME.setText(intent.getStringExtra("name"));

    }
}
