package com.led.project;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    //widget
    Button btnPaired;
    ListView deviceslist;
    //bluetooth
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //call the widgets
        btnPaired = (Button)findViewById(R.id.button);
        deviceslist = (ListView)findViewById(R.id.listView);
        //set bluetooth when its available
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        //bluetooth notification
        if(myBluetooth == null)
        {
            //show message
            Toast.makeText(getApplicationContext(),"Bluetooth Device not Available",Toast.LENGTH_LONG).show();
            finish();


        }
        else if(!myBluetooth.isEnabled())
        {
            //ask for turning on bluetooth
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            //show List
            public void onClick(View v) {
                pairedDevicesList();

            }
        });
    }
    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();
        if(pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No Paired Devices Found",Toast.LENGTH_LONG).show();

        }
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        deviceslist.setAdapter(adapter);
        deviceslist.setOnItemClickListener(myListClickListener);

    }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            //get MAC address
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            //intent to next chap
            Intent i = new Intent(MainActivity.this, Main2Activity.class);
            //change chapter
            i.putExtra(EXTRA_ADDRESS,address);
            startActivity(i);

        }


    };
}
