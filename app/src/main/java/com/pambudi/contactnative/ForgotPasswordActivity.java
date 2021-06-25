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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    final String TAG = "ForgotPasswordActivity";
    private String email;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText edtEmail = findViewById(R.id.edtEmail);
        Button btnForgotPassword = findViewById(R.id.btnForgotPassword);

        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edtEmail.getText().toString();

                boolean isEmptyFields = false;

                if (TextUtils.isEmpty(email)) {
                    isEmptyFields = true;
                    edtEmail.setError("Email Tidak boleh kosong");
                }

                if (!isEmptyFields){
                    loading = ProgressDialog.show(
                            ForgotPasswordActivity.this,
                            null,
                            "Loading",
                            true,
                            false
                    );

                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Password reset form has been sent to email",
                                                Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed reset password.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    loading.dismiss();
                                }
                            });
                }
            }
        });
    }
}