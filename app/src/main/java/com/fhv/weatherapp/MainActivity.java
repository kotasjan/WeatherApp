package com.fhv.weatherapp;

import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;


import com.fhv.weatherapp.model.DataModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private ListView listView;
    private TextView cityHeaderName;

    ArrayList<DataModel> dataModels;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);



        listView = (ListView)findViewById(R.id.list);
        dataModels= new ArrayList<>();
        dataModels.add(new DataModel("Dornbirn", "26", "snow"));
        dataModels.add(new DataModel("Dornbirn", "25", "snow"));
        dataModels.add(new DataModel("Dornbirn", "23", "snow"));
        dataModels.add(new DataModel("Dornbirn","21", "snow"));
        adapter = new CustomAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
    }



    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

}