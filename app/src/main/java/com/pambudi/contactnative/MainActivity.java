package com.pambudi.contactnative;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    boolean status = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvStatusVerification = findViewById(R.id.tvStatusVerification);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            status = user.isEmailVerified();
            String email = user.getEmail();
            tvEmail.setText(email);
            if (status){
                tvStatusVerification.setText("Your account has been verified");
            }else {
                tvStatusVerification.setText("Your account is not verified");
            }
        }

    }

}