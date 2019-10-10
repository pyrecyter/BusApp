package com.example.busapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private EditText phonenumber,code;
    private String PhoneNumber,Code,verificationID;
    private Button validate,send,register;
    private ProgressBar progressBar;
    private TextView lable;
    private boolean checking;
    private User currentUser;

    private Validations validations;


    private FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        phonenumber = findViewById(R.id.reg_pas_phn);
        code = findViewById(R.id.pas_reg_code);
        validate = findViewById(R.id.pas_reg_validatebtn);
        send = findViewById(R.id.pasloginBtn);
        progressBar = findViewById(R.id.pas_reg_progressBar);
        lable = findViewById(R.id.pas_reg_lable);
        register = findViewById(R.id.registerBtn);

        code.setVisibility(View.GONE);
        validate.setVisibility(View.GONE);
        lable.setVisibility(View.GONE);
        verificationID = "";
        validations = new Validations();

        firebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String smscode = phoneAuthCredential.getSmsCode();
                if(smscode != null){
                    Toast.makeText(Login.this, "Verification done !",Toast.LENGTH_LONG).show();

                    login(phoneAuthCredential);

                }else{
                    Toast.makeText(Login.this, "Enter the code sent to " + PhoneNumber, Toast.LENGTH_LONG).show();
                    code.setVisibility(View.VISIBLE);
                    validate.setVisibility(View.VISIBLE);
                    lable.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    login(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Login.this,e.toString(),Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId,token);
                verificationID = verificationId;
            }
        };

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                checking = true;
                PhoneNumber = phonenumber.getText().toString();
                if(validations.phoneNumberValidation(PhoneNumber)){
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                    progressBar.setVisibility(View.VISIBLE);
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String num = ds.child("phoneNumber").getValue(String.class);
                                if(num != null)
                                    if (num.matches(PhoneNumber)) {
                                        checking = false;
                                    }
                            }
                            if (!checking) {
                                PhoneNumber = "+94" + PhoneNumber.substring(1,10);
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber, 10, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);

                                validate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Code = code.getText().toString();
                                        progressBar.setVisibility(View.VISIBLE);
                                        if (Code.matches(verificationID)) {
                                            Toast.makeText(view.getContext(),"Verification done !",Toast.LENGTH_LONG).show();
                                            code.setVisibility(View.GONE);
                                            validate.setVisibility(View.GONE);
                                            lable.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(Login.this, "Verification Failed !", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(Login.this, "Not Registered !", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{ Toast.makeText(view.getContext(),"Invalid Phone Number !",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);}
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Login.this,Register.class);
                startActivity(it);
            }
        });
    }
    public void login(PhoneAuthCredential credential){

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            currentUser = dataSnapshot.child(user.getUid()).getValue(User.class);
                            code.setVisibility(View.GONE);
                            validate.setVisibility(View.GONE);
                            lable.setVisibility(View.GONE);
                            if(currentUser.getType().matches("Driver")){
                                Toast.makeText(Login.this,"Logged in as Bus Driver !",Toast.LENGTH_LONG).show();
                                Intent it = new Intent(Login.this,NavPannel.class);
                                it.putExtra("token",user.getUid());
                                startActivity(it);
                            }else{
                                Toast.makeText(Login.this,"Logged in as Passenger",Toast.LENGTH_LONG).show();
                                Intent it = new Intent(Login.this,PasHome.class);
                                it.putExtra("token",user.getUid());
                                startActivity(it);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(Login.this,"Error !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }
}
