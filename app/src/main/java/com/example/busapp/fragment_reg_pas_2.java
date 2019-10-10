package com.example.busapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_reg_pas_2 extends Fragment {

    private EditText phonenumber, code;
    private String PhoneNumber, Code, verificationID;
    private Button validate, register, send;
    private ProgressBar progressBar;
    private TextView lable;
    private boolean checking;

    private Validations validations;
    private View view;


    private FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_reg_pas_2, container, false);


        phonenumber = view.findViewById(R.id.reg_pas_phn);
        code = view.findViewById(R.id.pas_reg_code);
        validate = view.findViewById(R.id.pas_reg_validatebtn);
        register = view.findViewById(R.id.pas_reg_registerBtn);
        send = view.findViewById(R.id.pasloginBtn);
        progressBar = view.findViewById(R.id.pas_reg_progressBar);
        lable = view.findViewById(R.id.pas_reg_lable);


        progressBar.setVisibility(View.INVISIBLE);
        code.setVisibility(View.INVISIBLE);
        validate.setVisibility(View.INVISIBLE);
        register.setVisibility(View.INVISIBLE);
        lable.setVisibility(View.INVISIBLE);
        verificationID = "";
        validations = new Validations();

        firebaseAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String smscode = phoneAuthCredential.getSmsCode();
                if(smscode != null){
                    register.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);
                    validate.setVisibility(View.INVISIBLE);
                    code.setVisibility(View.INVISIBLE);
                    lable.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(view.getContext(),"Verification done !",Toast.LENGTH_LONG).show();

                    login(phoneAuthCredential);

                }else{

                    Toast.makeText(view.getContext(), "Enter the code sent to " + PhoneNumber, Toast.LENGTH_LONG).show();
                    code.setVisibility(View.VISIBLE);
                    validate.setVisibility(View.VISIBLE);
                    lable.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                    login(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(view.getContext(),"Verification Failed !" ,Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(view.getContext(), "Phone Number Already Registered!", Toast.LENGTH_LONG).show();
                                        checking = false;
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                            }
                            if (checking) {
                                PhoneNumber = "+94" + PhoneNumber.substring(1,10);
                                PhoneAuthProvider.getInstance().verifyPhoneNumber(PhoneNumber, 10, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);

                                validate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Code = code.getText().toString();
                                        if (Code.matches(verificationID)) {
                                            Toast.makeText(view.getContext(),"Verification done !",Toast.LENGTH_LONG).show();
                                            code.setVisibility(View.INVISIBLE);
                                            validate.setVisibility(View.INVISIBLE);
                                            register.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(view.getContext(), "Verification Failed !", Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{ Toast.makeText(view.getContext(),"Invalid Phone Number !",Toast.LENGTH_LONG).show(); }
            }
        });

        return view;
    }

    public void login(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener((Register) view.getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    ((Register)getActivity()).setPhone(PhoneNumber);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    ((Register)getActivity()).addPasRegister(user.getUid());
                }else{
                    Toast.makeText(view.getContext(),"Error !",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
