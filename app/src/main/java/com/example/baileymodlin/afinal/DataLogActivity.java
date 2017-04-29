package com.example.baileymodlin.afinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import com.example.baileymodlin.afinal.BluetoothConnection;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.fuel.WidebandAirFuelRatioCommand;
import com.github.pires.obd.commands.pressure.BarometricPressureCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.temperature.AirIntakeTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class DataLogActivity extends AppCompatActivity{

    private EditText airFuelEdit;
    private EditText rpmEdit;
    private EditText fuelEdit;
    private EditText tempEdit;
    private EditText pressure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.dataLogToolbar);
        airFuelEdit= (EditText) findViewById(R.id.airFuelEditText);
        rpmEdit = (EditText) findViewById(R.id.rpmEditText);
        fuelEdit = (EditText) findViewById(R.id.fuelEditText);
        tempEdit = (EditText) findViewById(R.id.iatEditText);
        pressure = (EditText) findViewById(R.id.boostVacPressEditText);
        setSupportActionBar(toolbar);
        BluetoothConnection bluetoothConnection = new BluetoothConnection(this, rpmEdit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        airFuelEdit.setText(bluetoothConnection.getAirFuel());
        rpmEdit.setText(bluetoothConnection.getRpm());
        fuelEdit.setText(bluetoothConnection.getFuel());
        tempEdit.setText(bluetoothConnection.getIatTemp());
        pressure.setText(bluetoothConnection.getBaroPressure());

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        if(menuItem.getItemId() == android.R.id.home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }
}
