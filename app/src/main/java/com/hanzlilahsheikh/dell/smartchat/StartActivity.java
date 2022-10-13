package com.hanzlilahsheikh.dell.smartchat;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    private Button mRegButton;
    private Button mSignInButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegButton=findViewById(R.id.start_reg_button);
        mSignInButton=findViewById(R.id.start_signin_button);
        progressBar  = findViewById(R.id.progress_bar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               progressBar.setVisibility(View.GONE);

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
        }, 1500);


    }
}
