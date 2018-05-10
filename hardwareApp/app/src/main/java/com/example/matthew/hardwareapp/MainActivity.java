package com.example.matthew.hardwareapp;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int level = 1;
    private boolean inputStarted = false;
    private ArrayList<Integer> commandList = new ArrayList<>();
    private ArrayList<Integer> inputList = new ArrayList<>();
    int position = 0;

    private static final String TAG = "Hardware App";
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListener;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Using proximity sensor
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        //close if no prox sensor
        if(proximitySensor == null) {
            Log.e(TAG, "Proximity sensor not available.");
            finish();
        }

        // Using gyroscope sensor
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //close if no gyro sensor
        if(gyroscopeSensor == null) {
            Log.e(TAG, "Gyroscope sensor not available.");
            finish();
        }

        proximitySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[0] < proximitySensor.getMaximumRange() && inputStarted) {
                    inputList.add(4);
                    TextView lip = findViewById(R.id.lastInput);
                    lip.setText("Last Input = Proximity Sensor");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { }
        };

        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.values[2] > 0.5f && inputStarted) { // anticlockwise
                    inputList.add(5);
                    TextView lip = findViewById(R.id.lastInput);
                    lip.setText("Last Input = Gyro Sensor");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) { }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(proximitySensorListener,
                proximitySensor, 1500000);
        sensorManager.registerListener(gyroscopeSensorListener,
                gyroscopeSensor, 1500000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(proximitySensorListener);
        sensorManager.unregisterListener(gyroscopeSensorListener);
    }

    public void increaseLevel(View v)
    {
        if(level < 20)
        {
            level++;
        }
        TextView tv = findViewById(R.id.textView);
        tv.setText("Level = " + Integer.toString(level));
    }

    public void decreaseLevel(View v)
    {
        if(level > 1)
        {
            level--;
        }
        TextView tv = findViewById(R.id.textView);
        tv.setText("Level = " + Integer.toString(level));
    }

    public void resetLevel(View v)
    {
        TextView tv = findViewById(R.id.textView);
        level = 1;
        tv.setText("Level = " + Integer.toString(level));
    }

    public void generateList(View v)
    {
        commandList.clear();
        inputList.clear();
        position = 0;
        int lastNumber = 998;
        int newNumber = 999;
        Random rand = new Random();
        for(int i = 0; i < level; i++)
        {
            newNumber = rand.nextInt(5) + 1;
            while(newNumber == lastNumber)
            {
                newNumber = rand.nextInt(5) + 1;
            }
            lastNumber = newNumber;
            commandList.add(newNumber);
        }
        //this denotes the end of the commands
        commandList.add(9999);
        displayCmdList();
    }

    public void redButton(View v)
    {
        if(inputStarted)
        {
            inputList.add(1);
            TextView lip = findViewById(R.id.lastInput);
            lip.setText("Last Input = Red Button Pushed");
        }
    }

    public void blueButton(View v)
    {
        if(inputStarted)
        {
            inputList.add(2);
            TextView tv = findViewById(R.id.lastInput);
            tv.setText("Last Input = Blue Button Pushed");
        }
    }

    public void greenButton(View v)
    {
        if(inputStarted)
        {
            inputList.add(3);
            TextView tv = findViewById(R.id.lastInput);
            tv.setText("Last Input = Green Button Pushed");
        }
    }

    public void startInputs(View v)
    {
        Button b = findViewById(R.id.inputStartStop);
        TextView li = findViewById(R.id.lastInput);
        if(inputStarted)
        {
            b.setText("Start Input");
            inputStarted = false;
        }
        else
        {
            li.setText("");
            b.setText("Stop Input");
            inputStarted = true;
        }
    }

    public void checkInput(View v)
    {
        //adding 9999 on the end, same as command list
        inputList.add(9999);
        compareInputOutput(v);
    }

    public void resetInputs(View v){
        TextView li = findViewById(R.id.lastInput);
        li.setText("Inputs Reset");
        inputList.clear();
    }

    public void compareInputOutput(View v)
    {
        //Compare the inputs given
        boolean isSame = commandList.equals(inputList);
        TextView li = findViewById(R.id.lastInput);

        if(isSame)
        {
            li.setText("Inputs are the same! \nCongratulations!!");
        }
        else
        {
            li.setText("Inputs are not the same!");
        }
    }

    public void displayCmdList()
    {
        final TextView tv = findViewById(R.id.middleTextBox);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < commandList.size(); i++) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String printout = "";
                                switch (commandList.get(position)) {
                                    case 1:
                                        printout = "red";
                                        break;
                                    case 2:
                                        printout = "blue";
                                        break;
                                    case 3:
                                        printout = "green";
                                        break;
                                    case 4:
                                        printout = "Prox sensor";
                                        break;
                                    case 5:
                                        printout = "Gyro Sensor (left or right)";
                                        break;
                                    case 9999:
                                        printout = "";
                                        break;
                                    default:
                                        break;
                                }

                                //if final command (denoted by 9999), then write "go" so last command can't be seen
                                if(commandList.get(position) == 9999)
                                {
                                    tv.setText("Go!");
                                } else {
                                    tv.setText("Command is = " + printout + "\nCommand Number = " + position);
                                    position++;
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }
}

