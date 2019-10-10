package com.example.busapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private SectionStatePagerAdaptor sectionStatePagerAdaptor;
    private ViewPager viewPager;
    private String name,number,routeNo,phone,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sectionStatePagerAdaptor = new SectionStatePagerAdaptor(getSupportFragmentManager());
        viewPager = findViewById(R.id.registerView);
        setViewPagerAdaptor(viewPager);
    }

    private void setViewPagerAdaptor(ViewPager viewPager){
        SectionStatePagerAdaptor adaptor = new SectionStatePagerAdaptor(getSupportFragmentManager());
        adaptor.addFragment(new registerSelection(), "Register mode");
        adaptor.addFragment(new fragment_reg_bus_1(), "bus Reg 1");
        adaptor.addFragment(new fragment_reg_pas_1(), "Passenger Reg 1");
        adaptor.addFragment(new fragment_reg_bus_2(), "bus Reg 2");
        adaptor.addFragment(new fragment_reg_pas_2(),"Passenger Reg 2");
        viewPager.setAdapter(adaptor);
    }

    public void setViewPager(int fragment){
        viewPager.setCurrentItem(fragment);
    }

    public void setOptions(String name,String number,String routeNo){
        this.name = name;
        this.number = number;
        this.routeNo = routeNo;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public void addBusRegister(String UID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        phone = "0" + phone.substring(3,12);
        databaseReference.child(UID).setValue(new User(name,phone,"Driver",number,routeNo,0.0,0.0,false));
        Toast.makeText(this, "Successfully Registered ! ", Toast.LENGTH_LONG).show();
    }
    public void setPasOptions(String email,String Name){
        this.email = email;
        this.name = Name;
    }
    public void addPasRegister(String UID){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        phone = "0" + phone.substring(3,12);
        databaseReference.child(UID).setValue(new User(name,email,phone,"Passenger"));
        Toast.makeText(this, "Successfully Registered ! ", Toast.LENGTH_LONG).show();
    }
}
