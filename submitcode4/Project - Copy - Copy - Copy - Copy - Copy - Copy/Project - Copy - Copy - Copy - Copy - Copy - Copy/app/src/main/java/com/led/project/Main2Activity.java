package com.led.project;

import android.support.v7.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class Main2Activity extends AppCompatActivity {
    ProgressBar prg;
    Button btnOn,btnOff,btn1,btn2,btn3,btn4,btn5;
    TextView txtString, sensorView0,dataSensor,sensorView1;
    Handler bluetoothIn;
    String sensor0,sensor1;
    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //Link the buttons and textViews to respective views
        btnOn = (Button) findViewById(R.id.buttonOn);
        btnOff = (Button) findViewById(R.id.buttonOff);
        txtString = (TextView) findViewById(R.id.txtString);
        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        sensorView1= (TextView)findViewById(R.id.dust);
        dataSensor = (TextView) findViewById(R.id.show);
        prg = (ProgressBar)findViewById(R.id.progressBar);
        btn1 = (Button) findViewById(R.id.button2);
        btn1.setVisibility(View.INVISIBLE);
        btn2 = (Button) findViewById(R.id.button3);
        btn2.setVisibility(View.INVISIBLE);
        btn3 = (Button) findViewById(R.id.button4);
        btn3.setVisibility(View.INVISIBLE);
        btn4 = (Button) findViewById(R.id.button6);
        btn4.setVisibility(View.INVISIBLE);
        btn5 = (Button) findViewById(R.id.button5);
        btn5.setVisibility(View.INVISIBLE);
        //led.text.setTextColor(R.color.Red);

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                        // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(10, endOfLineIndex);    // extract string
                        txtString.setText("Waring:" + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data receive

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-
                            sensor1 = recDataString.substring(5,10);
                            sensorView0.setText(" UV Intensity (mW/cm^2) = " + sensor0);   //update the textviews with sensor value
                            sensorView1.setText("Dust desity (mg/m^3) = "+sensor1);
                            float i = Float.parseFloat(recDataString.substring(1, 5));
                            float data = (i / 12) * 100;
                            dataSensor.setText(i + "/12");
                            int j = (int) data;
                            prg.setProgress(j);
                            if(i<0.01)
                            {
                                btn1 = (Button) findViewById(R.id.button2);
                                btn1.setVisibility(View.INVISIBLE);
                                btn2 = (Button) findViewById(R.id.button3);
                                btn2.setVisibility(View.INVISIBLE);
                                btn3 = (Button) findViewById(R.id.button4);
                                btn3.setVisibility(View.INVISIBLE);
                                btn4 = (Button) findViewById(R.id.button6);
                                btn4.setVisibility(View.INVISIBLE);
                                btn5 = (Button) findViewById(R.id.button5);
                                btn5.setVisibility(View.INVISIBLE);
                            }
                            if (i >= 0.01 && i <= 2.9) // if data sensor that is from 0 to 2.9, button1 will enable view
                            {
                                btn1 = (Button) findViewById(R.id.button2);
                                btn1.setVisibility(View.VISIBLE);
                                btn2 = (Button) findViewById(R.id.button3);
                                btn2.setVisibility(View.INVISIBLE);
                                btn3 = (Button) findViewById(R.id.button4);
                                btn3.setVisibility(View.INVISIBLE);
                                btn4 = (Button) findViewById(R.id.button6);
                                btn4.setVisibility(View.INVISIBLE);
                                btn5 = (Button) findViewById(R.id.button5);
                                btn5.setVisibility(View.INVISIBLE);
                            }

                            if (i > 2.9 && i <= 5.9)//if data sensor that is from 2.9 to 5.9, button2 will enable view
                            {
                                btn1 = (Button) findViewById(R.id.button2);
                                btn1.setVisibility(View.VISIBLE);
                                btn2 = (Button) findViewById(R.id.button3);
                                btn2.setVisibility(View.VISIBLE);
                                btn3 = (Button) findViewById(R.id.button4);
                                btn3.setVisibility(View.INVISIBLE);
                                btn4 = (Button) findViewById(R.id.button6);
                                btn4.setVisibility(View.INVISIBLE);
                                btn5 = (Button) findViewById(R.id.button5);
                                btn5.setVisibility(View.INVISIBLE);

                            }
                            if (i > 5.9 && i <= 7.9)//if data sensor that is from 5.9 to 7.9, button3 will enable view
                            {
                                btn1 = (Button) findViewById(R.id.button2);
                                btn1.setVisibility(View.VISIBLE);
                                btn2 = (Button) findViewById(R.id.button3);
                                btn2.setVisibility(View.VISIBLE);
                                btn3 = (Button) findViewById(R.id.button4);
                                btn3.setVisibility(View.VISIBLE);
                                btn4 = (Button) findViewById(R.id.button6);
                                btn4.setVisibility(View.INVISIBLE);
                                btn5 = (Button) findViewById(R.id.button5);
                                btn5.setVisibility(View.INVISIBLE);
                            }
                            if (i > 7.9 && i <= 10.9)////if data sensor that is from 7.9 to 10.9, button4 will enable view
                            {
                                btn1 = (Button) findViewById(R.id.button2);
                                btn1.setVisibility(View.VISIBLE);
                                btn2 = (Button) findViewById(R.id.button3);
                                btn2.setVisibility(View.VISIBLE);
                                btn3 = (Button) findViewById(R.id.button4);
                                btn3.setVisibility(View.VISIBLE);
                                btn4 = (Button) findViewById(R.id.button6);
                                btn4.setVisibility(View.VISIBLE);
                                btn5 = (Button) findViewById(R.id.button5);
                                btn5.setVisibility(View.INVISIBLE);
                            }
                            if (i > 10.9) {
                                //if data sensor more than 11, button5 will enable view
                                btn1 = (Button) findViewById(R.id.button2);
                                btn1.setVisibility(View.VISIBLE);
                                btn2 = (Button) findViewById(R.id.button3);
                                btn2.setVisibility(View.VISIBLE);
                                btn3 = (Button) findViewById(R.id.button4);
                                btn3.setVisibility(View.VISIBLE);
                                btn4 = (Button) findViewById(R.id.button6);
                                btn4.setVisibility(View.VISIBLE);
                                btn5 = (Button) findViewById(R.id.button5);
                                btn5.setVisibility(View.VISIBLE);
                            }
                        }

                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        // Set up onClick listeners for buttons to send 1 or 2 to turn on/off LED

        btnOff.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mConnectedThread.write("0");
                    // Send "0" via Bluetooth
                }
            });
        btnOn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mConnectedThread.write("1");
                    // Send "1" via Bluetooth

                }
            });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}