package com.hanzlilahsheikh.dell.smartchat;

import android.app.ProgressDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;
    private DatabaseReference mStatusdatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String user_id=mCurrentUser.getUid();
        mStatusdatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mStatus=findViewById(R.id.status_input);
        mSavebtn=findViewById(R.id.status_change_button);
        mProgress=new ProgressDialog(this);
        mToolbar=findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_text=getIntent().getStringExtra("status_text");
        mStatus.getEditText().setText(status_text);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please Wait while We save Changes");
                mProgress.show();

                String status=mStatus.getEditText().getText().toString();
                mStatusdatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            mProgress.dismiss();
                        }
                        else
                        {
                            Toast.makeText(StatusActivity.this, "Got Some Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
