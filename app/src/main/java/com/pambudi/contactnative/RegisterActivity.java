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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    final String TAG = "RegisterActivity";
    private EditText edtName, edtEmail, edtPassword1, edtPassword2;
    private String name, email, password1, password2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private ProgressDialog loading;
    private String userID;
    final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    final String rexUppercase = "(?s)[^A-Z]*[A-Z].*";
    final String rexLowercase = "(?s)[^a-z]*[a-z].*";
    final String rexNumber = "(?s)[^0-9]*[0-9].*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword1 = findViewById(R.id.edtPassword1);
        edtPassword2 = findViewById(R.id.edtPassword2);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = edtName.getText().toString();
                email = edtEmail.getText().toString();
                password1 = edtPassword1.getText().toString();
                password2 = edtPassword2.getText().toString();

                boolean isEmptyFields = false;
                boolean validateEmailField = false;
                boolean isNotEqualsPassword = false;
                boolean isNotLongName = false;
                boolean isNotShortName = false;
                boolean isNotShortPassword = false;
                boolean isNotContainLowerUpper1 = false;
                boolean isNotContainLowerUpper2 = false;
                boolean isNotContainNumber1 = false;
                boolean isNotContainNumber2 = false;

                if (TextUtils.isEmpty(name)) {
                    isEmptyFields = true;
                    edtName.setError("Nama Tidak boleh kosong");
                }

                if (!TextUtils.isEmpty(name)){
                    if (name.length() < 3) {
                        isNotLongName = true;
                        edtName.setError("Nama Minimal 3 huruf");
                    }

                    if (name.length() > 50) {
                        isNotShortName = true;
                        edtName.setError("Nama Maksimal 50 huruf");
                    }
                }

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

                if (TextUtils.isEmpty(password1)) {
                    isEmptyFields = true;
                    edtPassword1.setError("Password Tidak boleh kosong");
                }

                if (!TextUtils.isEmpty(password1)){
                    if (!password1.matches(rexLowercase) || !password1.matches(rexUppercase)) {
                        isNotContainLowerUpper1 = true;
                        edtPassword1.setError("Password harus mengandung huruf kecil dan besar");
                    }

                    if (!password1.matches(rexNumber)){
                        isNotContainNumber1 = true;
                        edtPassword1.setError("Password harus mengandung angka");
                    }

                    if (password1.length() < 8) {
                        isNotShortPassword = true;
                        edtPassword1.setError("Password minimal 8 karakter");
                    }
                }

                if (TextUtils.isEmpty(password2)) {
                    isEmptyFields = true;
                    edtPassword2.setError("Confirm Password Tidak boleh kosong");
                }

                if (!TextUtils.isEmpty(password2)){
                    if (password2.length() < 8) {
                        isNotShortPassword = true;
                        edtPassword2.setError("Password minimal 8 karakter");
                    }

                    if (!password2.matches(rexLowercase) || !password1.matches(rexUppercase)) {
                        isNotContainLowerUpper2 = true;
                        edtPassword2.setError("Password harus mengandung huruf kecil dan besar");
                    }

                    if (!password2.matches(rexNumber)){
                        isNotContainNumber2 = true;
                        edtPassword2.setError("Password harus mengandung angka");
                    }

                    if (!password2.equals(password1)){
                        isNotEqualsPassword = true;
                        edtPassword2.setError("Kedua Password Harus Sama");
                    }
                }

                if (!isEmptyFields && !validateEmailField && !isNotEqualsPassword
                        && !isNotLongName && !isNotShortName && !isNotShortPassword
                        && !isNotContainLowerUpper1 && !isNotContainNumber1
                        && !isNotContainLowerUpper2 && !isNotContainNumber2){
                    loading = ProgressDialog.show(
                            RegisterActivity.this,
                            null,
                            "Loading",
                            true,
                            false
                    );
                    signUp(email, password1);
                }

            }
        });
    }

    private void signUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null){
                                Toast.makeText(RegisterActivity.this, "Registration Success.",
                                        Toast.LENGTH_SHORT).show();

                                user.sendEmailVerification();
                                userID = user.getUid();

                                DocumentReference documentReference = fStore.collection("users").document(userID);

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("email", email);
                                documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess : user profile created");
                                    }
                                });
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }

                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        loading.dismiss();
                    }
                });
    }
}