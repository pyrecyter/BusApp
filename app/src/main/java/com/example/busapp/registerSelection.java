package com.example.busapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class registerSelection extends Fragment {

    private Button bus,pas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_selection, container, false);
        bus = view.findViewById(R.id.bus_selectionBtn);
        pas = view.findViewById(R.id.passenger_selectionBtn);

        bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Register)getActivity()).setViewPager(1);
            }
        });
        pas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Register)getActivity()).setViewPager(2);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
