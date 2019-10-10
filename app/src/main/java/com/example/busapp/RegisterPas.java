package com.example.busapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPas extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText name;
    private EditText phone;
    private EditText password;
    private EditText email;
    private Button signup;
    private Validations validations;


    private String Name,Phone,Password,Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pas);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.reg_pas_name);
        phone = findViewById(R.id.reg_pas_phone);
        email = findViewById(R.id.reg_pas_email);
        password = findViewById(R.id.reg_pas_password);
        signup = findViewById(R.id.reg_pas_confirm);

        firebaseAuth = FirebaseAuth.getInstance();

        validations = new Validations();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name = name.getText().toString();
                Phone = phone.getText().toString();
                Email = email.getText().toString();
                Password = password.getText().toString();

                if(validations.EmailValidation(Email) && validations.notNullValidate(Name)){
                    if(validations.phoneNumberValidation(Phone)){
                        if(validations.passwordValidation(Password)) {
                            firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegisterPas.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PasUsers");
                                        databaseReference.child(user.getUid()).setValue(new PasUser(Name, Email, Phone));
                                        Toast.makeText(RegisterPas.this, "Sign up Successful !", Toast.LENGTH_LONG).show();
                                        Intent it = new Intent(RegisterPas.this, Login.class);
                                        startActivity(it);
                                    } else {
                                        Toast.makeText(RegisterPas.this, "Sign up Failed !", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(RegisterPas.this,"Password length should be 6 or more !",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(RegisterPas.this,"Enter a valid phone number !",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(RegisterPas.this,"Invalid Details",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
