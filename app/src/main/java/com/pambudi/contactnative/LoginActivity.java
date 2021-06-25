package com.pambudi.contactnative;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    final String TAG = "LoginActivity";
    private EditText edtEmail, edtPassword;
    private String email, password;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegistration = findViewById(R.id.tvRegistration);
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();

                boolean isEmptyFields = false;
                boolean validateEmailField = false;

                if (TextUtils.isEmpty(email)) {
                    isEmptyFields = true;
                    edtEmail.setError("Email Tidak boleh kosong");
                }

                if (!TextUtils.isEmpty(email)){
                    if (!email.trim().matches(emailPattern)) {
                        validateEmailField = true;
                        edtEmail.setError("Masukan format email dengan benar");
                    }
                }

                if (TextUtils.isEmpty(password)) {
                    isEmptyFields = true;
                    edtPassword.setError("Password Tidak boleh kosong");
                }

                if (!isEmptyFields && !validateEmailField){
                    loading = ProgressDialog.show(
                            LoginActivity.this,
                            null,
                            "Loading",
                            true,
                            false
                    );
                    signIn(email, password);
                }
            }
        });

        tvRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        loading.dismiss();
                    }
                });
    }
}