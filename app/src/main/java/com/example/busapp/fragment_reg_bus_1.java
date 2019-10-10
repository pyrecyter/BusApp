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
public class fragment_reg_bus_1 extends Fragment {

    private EditText name,numberplate,routeNumber;
    private String Name,NumberPlate,RouteNumber;
    private Button next;
    private ProgressBar progressBar;
    private boolean checking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_reg_bus_1, container, false);
        name = view.findViewById(R.id.pas_reg_name);
        numberplate = view.findViewById(R.id.pas_reg_email);
        routeNumber = view.findViewById(R.id.bus_reg_route);
        next = view.findViewById(R.id.pas_reg_next);
        progressBar = view.findViewById(R.id.pas_reg_1_progressBar);
        progressBar.setVisibility(View.INVISIBLE);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                checking = true;


                Name = name.getText().toString();
                NumberPlate = numberplate.getText().toString();
                RouteNumber = routeNumber.getText().toString();

                Validations validations = new Validations();

                if(validations.notNullValidate(Name) && validations.notNullValidate(RouteNumber)) {
                    if(validations.PlateValidation(NumberPlate)) {
                        progressBar.setVisibility(View.VISIBLE);
                        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String num = ds.child("numberPlate").getValue(String.class);
                                    if(num != null)
                                    if (num.matches(NumberPlate)) {
                                        Toast.makeText(view.getContext(), "Bus Already Registered!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        checking = false;
                                    }
                                }
                                if (checking) {
                                    ((Register)getActivity()).setOptions(Name,NumberPlate,RouteNumber);
                                    ((Register)getActivity()).setViewPager(3);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        Toast.makeText(view.getContext(),"Invalid Number Plate !",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(view.getContext(),"Please Fill All!",Toast.LENGTH_LONG).show();
                }
            }
        });



        return  view;
    }

}
