package com.example.baileymodlin.afinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

/**
 * Created by baileymodlin on 4/28/17.
 */

public class BluetoothConnection extends Thread{

    private BluetoothSocket socket;
    private final ArrayList devices = new ArrayList();
    private final ArrayList deviceString = new ArrayList();
    private String deviceAddress;
    private Context context;
    private String fuel;
    private String rpm;
    private String baroPressure;
    private String iatTemp;
    private String wideBand;
    private EditText mrpm;
    private final String rpmTag = "RPM";

    public BluetoothConnection(Context context, EditText rpm){
        this.context = context;
        mrpm = rpm;
        setUpBluetoothDevice();
        connectBluetoothDevice();
        getData();
    }

    private  void  setUpBluetoothDevice(){
        //Connect Bluetooth device
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices) {
                deviceString.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }

        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.select_dialog_singlechoice, deviceString.toArray(new String[deviceString.size()]));
        alert.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                int pos = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                deviceAddress = (String) devices.get(pos);
            }

        });

    }

    private void connectBluetoothDevice(){

        //Save Bluetooth device
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice device = btAdapter.getRemoteDevice("00:1D:A5:00:00:2C");

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try{
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getData(){
        try {
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
            RPMCommand rpmCommand = new RPMCommand();
            WidebandAirFuelRatioCommand airFuelCommand = new WidebandAirFuelRatioCommand();
            FuelLevelCommand fuelCommand = new FuelLevelCommand();
            AirIntakeTemperatureCommand aitCommand = new AirIntakeTemperatureCommand();
            BarometricPressureCommand baroPress = new BarometricPressureCommand();

           while (Thread.currentThread().isInterrupted()) {
                airFuelCommand.run(socket.getInputStream(), socket.getOutputStream());
                rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                aitCommand.run(socket.getInputStream(), socket.getOutputStream());
                fuelCommand.run(socket.getInputStream(), socket.getOutputStream());
                Log.d("DEBUG_0", "RPM: " + rpmCommand.getFormattedResult());

                mrpm.setText(rpmCommand.getFormattedResult());
                fuel = fuelCommand.getFormattedResult();
                rpm = rpmCommand.getFormattedResult();
                baroPressure = baroPress.getFormattedResult();
                iatTemp = aitCommand.getFormattedResult();
                wideBand = airFuelCommand.getFormattedResult();


                //airFuelEdit.setText(airFuelCommand.getFormattedResult());
                //rpmEdit.setText(airFuelCommand.getFormattedResult());
                //fuelEdit.setText(fuelCommand.getFormattedResult());
                //tempEdit.setText(aitCommand.getFormattedResult());
                //pressure.setText(baroPress.getFormattedResult());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getFuel(){
        return fuel;
    }

    public String getRpm(){
        return rpm;
    }
    public String getBaroPressure(){
        return baroPressure;
    }
    public String getAirFuel(){
        return wideBand;
    }
    public String getIatTemp(){
        return  iatTemp;
    }
}
