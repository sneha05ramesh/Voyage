package com.example.voyage;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText fullNameInput, emailInput, passwordInput, confirmPasswordInput;
    RadioGroup genderRadioGroup;
    Button createAccountButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        createAccountButton = findViewById(R.id.createAccountButton);

        createAccountButton.setOnClickListener(v -> {
            String name = fullNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();

            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            if (selectedGenderId == -1) {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedGenderButton = findViewById(selectedGenderId);
            String gender = selectedGenderButton.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase signup
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                // Store user details in Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Map<String, Object> userProfile = new HashMap<>();
                                userProfile.put("fullName", name);
                                userProfile.put("email", email);
                                userProfile.put("gender", gender);

                                db.collection("users").document(user.getUid())
                                        .set(userProfile)
                                        .addOnSuccessListener(aVoid -> {
                                            // Send email verification
                                            user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                                if (emailTask.isSuccessful()) {
                                                    Toast.makeText(this, "Account created. Please verify your email.", Toast.LENGTH_LONG).show();
                                                    mAuth.signOut();
                                                    finish();
                                                } else {
                                                    Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Signup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
