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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_reg_pas_1 extends Fragment {


    public fragment_reg_pas_1() {
        // Required empty public constructor
    }

    private EditText Name,Email;
    private  String name,email;
    private ProgressBar progressBar;
    private Button next;
    private boolean check;
    private Validations validations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragment_reg_pas_1, container, false);

        Name = view.findViewById(R.id.pas_reg_name);
        Email = view.findViewById(R.id.pas_reg_email);
        progressBar = view.findViewById(R.id.pas_reg_1_progressBar);
        next = view.findViewById(R.id.pas_reg_next);
        progressBar.setVisibility(View.INVISIBLE);

        validations = new Validations();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                check = true;

                name = Name.getText().toString();
                email = Email.getText().toString();

                if(!validations.notNullValidate(email) || validations.notNullValidate(name)){
                if(validations.EmailValidation(email)){
                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String num = ds.child("email").getValue(String.class);
                                if(num != null)
                                    if (num.matches(email)) {
                                        Toast.makeText(view.getContext(), "Email Already Registered!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        check = false;
                                    }
                            }
                            if (check) {
                                ((Register)getActivity()).setPasOptions(email,name);
                                ((Register)getActivity()).setViewPager(4);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    Toast.makeText(view.getContext(),"Invalid Email!",Toast.LENGTH_LONG).show();
                }}else{
                    Toast.makeText(view.getContext(),"Fill all!",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

}
