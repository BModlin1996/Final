/**
 * @author Bailey Modlin, Wyatt Davies
 * @version 1.0, 4/18/2017
 */
package com.example.baileymodlin.afinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up tool bar
        Toolbar appToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(appToolBar);

        //Setting up dash light button
        Button dashLightButton = (Button)findViewById(R.id.dashLightButton);
        dashLightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity(0);
            }
        });

        //Setting up Check engine button
        Button checkEngineButton = (Button)findViewById(R.id.celButton);
        checkEngineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity(1);
            }
        });

        //Setting up data log button
        Button datalogButton = (Button)findViewById(R.id.datalogButton);
        datalogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeActivity(2);
            }
        });

    }

    /**
     *
     * @param id
     */
    public void changeActivity(int id){
        switch (id){
            case 0:
                Intent intent = new Intent(this, DashLightActivity.class);
                startActivity(intent);
                break;
            case 1: ; break;
            case 2: ; break;

        }
    }

    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     *
     * @param menuItem
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemId = menuItem.getItemId();
        switch (itemId){

        }
        return false;
    }
}
