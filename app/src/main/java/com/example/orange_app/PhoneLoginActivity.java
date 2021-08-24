package com.example.orange_app;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button SendVerificationCodeButton, VerifyButton;
    private EditText InputPhoneNumber, InputVerificationCode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        SendVerificationCodeButton = (Button) findViewById(R.id.send_ver_code_button);
        VerifyButton = (Button) findViewById(R.id.verify_button);
        InputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        InputVerificationCode = (EditText) findViewById(R.id.verification_code_input);
        loadingBar = new ProgressDialog(this);

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String phoneNumber = InputPhoneNumber.getText().toString();

                if(TextUtils.isEmpty(phoneNumber)){

                    Toast.makeText(PhoneLoginActivity.this, "Введите номер телефона...", Toast.LENGTH_SHORT).show();

                }//if
                else{

                    loadingBar.setTitle("Подтверждение номера телефона");
                    loadingBar.setMessage("Подождите, пока мы проверяем ваш номер телефона...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            callbacks
                    );

                }//else

            }//onClick

        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                String verificationCode = InputVerificationCode.getText().toString();

                if(TextUtils.isEmpty(verificationCode)){

                    Toast.makeText(PhoneLoginActivity.this, "Введите код подтверждения...", Toast.LENGTH_SHORT).show();

                }//if
                else{

                    loadingBar.setTitle("Код подтверждения");
                    loadingBar.setMessage("Подождите, пока мы проверяем ваш код подтверждения...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }//else

            }//onClick

        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }//onVerificationCompleted

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Недействительный номер телефона, введите номер с кодом страны...", Toast.LENGTH_SHORT).show();

                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                VerifyButton.setVisibility((View.VISIBLE));
                InputVerificationCode.setVisibility(View.VISIBLE);

            }//onVerificationFailed

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Код был отправлен...", Toast.LENGTH_SHORT).show();

                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                VerifyButton.setVisibility((View.VISIBLE));
                InputVerificationCode.setVisibility(View.VISIBLE);

            }//onCodeSent


        };

    }//onCreate

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Вы успешно вошли в приложение...", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();

                        }//if
                        else {

                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();

                        }//else

                    }//onComplete

                });

    }//signInWithPhoneAuthCredential

    private void SendUserToMainActivity(){

        Intent mainActivity = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();

    }//SendUserToMainActivity

}//PhoneLoginActivity
