package com.hanzlilahsheikh.dell.smartchat;

import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName,mEmail,mPassword;
    private Button mCreateButton;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDisplayName=findViewById(R.id.reg_display_name);
        mEmail=findViewById(R.id.reg_email);
        mPassword=findViewById(R.id.reg_password);
        mRegProgress=new ProgressDialog(this);

        mCreateButton=findViewById(R.id.reg_createaccount);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle("Create Account");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name=mDisplayName.getEditText().getText().toString();
                String email=mEmail.getEditText().getText().toString();
                String password=mPassword.getEditText().getText().toString();
                if (!TextUtils.isEmpty(display_name)&& !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) )
                {
                    mRegProgress.setTitle("Registering user");
                    mRegProgress.setMessage("Please Wait While We Create Your Account ! ");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name,email,password);
                }
                else if (TextUtils.isEmpty(display_name))
                {
                    Toast.makeText(RegisterActivity.this, "Enter a User Name", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(RegisterActivity.this, "Enter an Email", Toast.LENGTH_SHORT).show();
                }
               else if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "You must enter a password", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void register_user(final String display_name, String email, String password) {
       mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful())
               {

                   FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                   String user_id=current_user.getUid();
                   mDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                   String device_token=FirebaseInstanceId.getInstance().getToken();
                   HashMap<String, String> userMap=new HashMap<>();
                   userMap.put("name",display_name);
                   userMap.put("status","Hi there,I am Using SmartChat");
                   userMap.put("image","default");
                   userMap.put("thumb_image","default");
                   userMap.put("device_token",device_token);
                   mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful())
                           {
                               mRegProgress.dismiss();

                               Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                               mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(mainIntent);
                               finish();


                           }
                       }
                   });



               }
               else
               {

                  String error="";
                   try {
                       throw task.getException();
                   }catch (FirebaseAuthWeakPasswordException e)
                   {
                        error="Weak Password";
                   } catch (FirebaseAuthInvalidCredentialsException e)
                   {
                       error="Invalid Email";
                   }
                   catch (FirebaseAuthUserCollisionException e)
                   {
                     error="User Already Exists";
                   }catch (Exception e) {
                       error="Authentication Failed\nPlease Check The Form & Try Again";
                       e.printStackTrace();
                   }

                   Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                   mRegProgress.hide();
               }
           }
       });

    }
}
