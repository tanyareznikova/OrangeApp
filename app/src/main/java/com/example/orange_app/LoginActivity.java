package com.example.orange_app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView NeedNewAccountLink, ForgetPasswordLink;

    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        InitializeFields();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
                //finish();
            }//onClick
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }//onClick
        });



        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent phoneLoginIntent = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phoneLoginIntent);

            }//onClick

        });

    }//onCreate

    /*
    private void showForgetPasswordDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Восстановление Пароля");

        LinearLayout linearLayout = new LinearLayout(this);

        final EditText emailEditText = new EditText(this);
        emailEditText.setHint("Почта");
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEditText.setMinEms(16);

        linearLayout.addView(emailEditText);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);

        builder.setPositiveButton("Восстановить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = emailEditText.getText().toString().trim();
                beginRecovery(email);

            }//onClick

        });
        builder.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }//onClick

        });

        builder.create().show();

    }//showForgetPasswordDialog

    private void beginRecovery(String email) {

        loadingBar.setMessage("Отправляем Почту...");
        loadingBar.show();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                loadingBar.dismiss();

                if(task.isSuccessful()){

                    Toast.makeText(LoginActivity.this, "Почта отправлена успешно...", Toast.LENGTH_SHORT).show();

                }//if
                else {

                    Toast.makeText(LoginActivity.this, "Ошибка...", Toast.LENGTH_SHORT).show();

                }//else

            }//onComplete

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                loadingBar.dismiss();

                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }//onFailure
        });

    }//beginRecovery

     */

    private void AllowUserToLogin() {

        final String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Введите почту...", Toast.LENGTH_SHORT).show();
        }//if

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль...", Toast.LENGTH_SHORT).show();
        }//if

        else{

            loadingBar.setTitle("Вход");
            loadingBar.setMessage("Пожалуйста подождите...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                //FirebaseInstanceId.getInstance().getInstanceId();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                UsersRef.child(currentUserID).child("device_token")
                                      .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){

                                                    SendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this, "Вы вошли в аккаунт...", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();

                                                }//if

                                            }//onComplete

                                        });

                            }//if
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }//else

                        }//onComplete
                    });
        }//else

    }//AllowUserToLogin

    private void InitializeFields() {

        LoginButton = (Button) findViewById(R.id.login_button);
        PhoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);

    }//InitializeFields

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }//SendUserToMainActivity

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }//SendUserToRegisterActivity
}//LoginActivity
