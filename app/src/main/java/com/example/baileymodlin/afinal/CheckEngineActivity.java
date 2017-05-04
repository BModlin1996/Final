package com.example.baileymodlin.afinal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
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

public class CheckEngineActivity extends AppCompatActivity {

    private BluetoothSocket socket;
    private EditText celCode;
    private Context context;
    private String cel;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_engine);
        Toolbar toolbar = (Toolbar) findViewById(R.id.checkEngineToolbar);
        celCode = (EditText) findViewById(R.id.celEditText);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                setUpBluetoothDevice();
                connectBluetoothDevice();
                getData();
            }

            private  void  setUpBluetoothDevice(){
                //Connect Bluetooth device
                ArrayList deviceString = new ArrayList();
                final ArrayList devices = new ArrayList();

                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for (BluetoothDevice device : pairedDevices) {
                        deviceString.add(device.getName() + "\n" + device.getAddress());
                        devices.add(device.getAddress());
                    }

                }
                //final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.select_dialog_singlechoice, deviceString.toArray(new String[deviceString.size()]));
                /**
                 alert.setSingleChoiceItems(arrayAdapter, -1, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                int pos = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                deviceAddress = (String) devices.get(pos);
                }
                });

                 alert.setTitle("Choose Bluetooth device");
                 alert.show();
                 **/
            }

            private void connectBluetoothDevice(){

                //Save Bluetooth device
                BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

                BluetoothDevice device = btAdapter.getRemoteDevice("00:1D:A5:00:F7:61");

                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                try{
                    socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }

            public void getData(){
                try {
                    new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
                    //Creating a new thread for the obd to update too
                    Thread thread = new Thread();
                    //Changed 5-2-2017

                    TroubleCodesCommand troubleCodesCommand = new TroubleCodesCommand();

                    while (!thread.currentThread().isInterrupted()) {

                        troubleCodesCommand.run(socket.getInputStream(), socket.getOutputStream());

                        Log.d("DEBUG_0", "Trouble codes: " + troubleCodesCommand.getFormattedResult());

                        // Log.d("DEBUG_3", "Boost/Vac: " + pressureCommand.getFormattedResult());
                        cel = troubleCodesCommand.getFormattedResult();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                celCode.setText(cel);
                            }
                        });

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();


    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
