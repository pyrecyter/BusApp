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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class RegisterBus extends AppCompatActivity {

    private EditText name,phonenumber,numberplate,code,password,routeNumber;
    private String Name,PhoneNumber,NumberPlate,Code,Password,verificationID,RouteNumber;
    private Button validate,register,send;
    private ProgressBar progressBar;
    private TextView lable;

    private Validations validations;


    private FirebaseAuth firebaseAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bus);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.pas_reg_name);
        phonenumber = findViewById(R.id.bus_reg_phone);
        numberplate = findViewById(R.id.pas_reg_email);
        code = findViewById(R.id.pas_reg_code);
        validate = findViewById(R.id.pas_reg_validatebtn);
        register = findViewById(R.id.pas_reg_registerBtn);
        send = findViewById(R.id.pasloginBtn);
        password = findViewById(R.id.bus_reg_password);
        progressBar = findViewById(R.id.pas_reg_progressBar);
        lable = findViewById(R.id.pas_reg_lable);
        routeNumber = findViewById(R.id.bus_reg_route);

        code.setVisibility(View.GONE);
        validate.setVisibility(View.GONE);
        register.setVisibility(View.GONE);
        lable.setVisibility(View.GONE);

        verificationID = "";
        validations = new Validations();

        firebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String smscode = phoneAuthCredential.getSmsCode();
                if(smscode != null){
                    register.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);
                    validate.setVisibility(View.GONE);
                    code.setVisibility(View.GONE);
                    lable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterBus.this,"Verification done !",Toast.LENGTH_LONG).show();

                    login(phoneAuthCredential);

                }else{
                    progressBar.setVisibility(View.GONE);
                    login(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(RegisterBus.this,"Verification Failed !" ,Toast.LENGTH_LONG).show();
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
            public void onClick(View view) {
                Name = name.getText().toString();
                PhoneNumber = phonenumber.getText().toString();
                NumberPlate = numberplate.getText().toString().toUpperCase();
                Password = password.getText().toString();
                RouteNumber = routeNumber.getText().toString();

                if(validations.PlateValidation(NumberPlate)){

                    if(validations.notNullValidate(Name) && validations.notNullValidate(RouteNumber)){
                        if(validations.phoneNumberValidation(PhoneNumber)){
                            if(validations.passwordValidation(Password)) {

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber, 10, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);

                                progressBar.setVisibility(View.VISIBLE);

                                Toast.makeText(RegisterBus.this, "Enter the code sent to " + PhoneNumber, Toast.LENGTH_LONG).show();
                                code.setVisibility(View.VISIBLE);
                                validate.setVisibility(View.VISIBLE);
                                lable.setVisibility(View.VISIBLE);


                                validate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Code = code.getText().toString();
                                        if (Code.matches(verificationID)) {
                                            Toast.makeText(RegisterBus.this,"Verification done !",Toast.LENGTH_LONG).show();
                                            code.setVisibility(View.GONE);
                                            validate.setVisibility(View.GONE);
                                            register.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(RegisterBus.this, "Verification Failed !", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(RegisterBus.this,"Password length should be 6 or more !",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(RegisterBus.this,"Enter a valid phone number with country code !",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(RegisterBus.this,"Fill all !",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(RegisterBus.this,"Invalid Number Plate !",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void login(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BusUsers");
                    databaseReference.child(user.getUid()).setValue(new BusUser(NumberPlate, Name, 0.0,0.0,PhoneNumber,false,NumberPlate));
                    Toast.makeText(RegisterBus.this, "Logging In", Toast.LENGTH_LONG).show();
                    Intent it = new Intent(RegisterBus.this, MapsActivityBus.class);
                    it.putExtra("token",user.getUid());
                    startActivity(it);
                }else{
                    Toast.makeText(RegisterBus.this,"Error !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
