package com.example.busapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NavPannel extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private String token;
    private TextView logName;
    private TextView numberPlate;
    private boolean isLive = true;
    private String username = "Bus User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_pannel);
        token = getIntent().getStringExtra("token");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        logName = headerView.findViewById(R.id.nav_bus_userName);
        numberPlate = headerView.findViewById(R.id.nav_bus_number);
        logName.setText(username);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(token);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("name").getValue(String.class);
                logName.setText(username);
                numberPlate.setText(dataSnapshot.child("numberPlate").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MapsActivityBus()).commit();
        }
        if (isLive) {
            Intent dbservice = new Intent(this, dbUpdateService.class);
            dbservice.putExtra("token", token);
            startService(dbservice);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent dbservice = new Intent(this, dbUpdateService.class);
        switch (menuItem.getItemId()) {
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapsActivityBus()).commit();
                break;
            case R.id.nav_gps:
                setLive(menuItem);
                break;
            case R.id.nav_exit:
                stopService(dbservice);
                finishAndRemoveTask();
                moveTaskToBack(true);
                break;
            case R.id.nav_logout:
                stopService(dbservice);
                FirebaseAuth.getInstance().signOut();
                Intent it = new Intent(this,Login.class);
                startActivity(it);
                finish();
                break;
            case R.id.nav_profile:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setLive(MenuItem menuItem) {
        isLive = !isLive;

        Intent dbservice = new Intent(this, dbUpdateService.class);
        if (isLive) {
            startService(dbservice);
            menuItem.setTitle("Live Off");
            menuItem.setIcon(R.drawable.ic_gps);
            Toast.makeText(this,"You are on Live",Toast.LENGTH_LONG).show();
        } else {
            stopService(dbservice);
            menuItem.setTitle("Go Live");
            menuItem.setIcon(R.drawable.ic_gps_off);
            Toast.makeText(this,"You are not on Live",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onDestroy() {
        Intent dbservice = new Intent(this, dbUpdateService.class);
        stopService(dbservice);
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }
}
