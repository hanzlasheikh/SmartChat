package com.example.dell.smartchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    private Button mRegButton;
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegButton=findViewById(R.id.start_reg_button);
        mSignInButton=findViewById(R.id.start_signin_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginIntent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(LoginIntent);
            }
        });
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent=new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });
    }
}
