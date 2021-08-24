package com.example.orange_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button ChangePasswordButton;
    private EditText UserEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    //private ProgressDialog loadingBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        ChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = UserEmail.getText().toString();

                if(email.equals("")){
                    Toast.makeText(ForgotPasswordActivity.this, "Напишите свою почту!", Toast.LENGTH_SHORT).show();
                }//if
                else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this, "Пожалуйста, проверьте свою почту.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                            }//if
                            else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }//else

                        }//onComplete
                    });
                }//else

                ChangePassword();

            }//onClick

        });//ChangePasswordButton.setOnClickListener

    }//onCreate

    private void ChangePassword() {



    }//ChangePassword

    private void InitializeFields() {

        toolbar = findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Восстановление пароля");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChangePasswordButton = (Button) findViewById(R.id.forgot_password_change_password_btn);
        UserEmail = (EditText) findViewById(R.id.forgot_password_email);
        //loadingBar = new ProgressDialog(this);

    }//InitializeFields

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }//SendUserToMainActivity

}//ForgotPasswordActivity
