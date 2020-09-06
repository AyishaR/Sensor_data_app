package com.example.distance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView axText, ayText, azText, gxText, gyText, gzText, fileTextDisplay;
    private SensorManager sensorManager;
    private Sensor accSensor, gyrSensor;
    private boolean isAccelerometerSensorAvailable, isGyrometerSensorAvailable, record, fileChoose, newFile;
    private Button recordButton, fileButton;
    PrintWriter writer = null;
    EditText gestureText, filenameText;
    float ax= 0, ay = 0, az = 0, gx = 0, gy = 0, gz = 0;
    int count = 0;
    String r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        axText = findViewById(R.id.textAX);
        ayText = findViewById(R.id.textAY);
        azText = findViewById(R.id.textAZ);

        gxText = findViewById(R.id.textGX);
        gyText = findViewById(R.id.textGY);
        gzText = findViewById(R.id.textGZ);

        gestureText = findViewById(R.id.TextGesture);

        filenameText = findViewById(R.id.fileText);

        fileTextDisplay = findViewById(R.id.FileNameDisplay);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
        {
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerSensorAvailable = true;
        }
        else
        {
            axText.setText("Accelerometer sensor not available!");
            isAccelerometerSensorAvailable = false;
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null)
        {
            gyrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            isGyrometerSensorAvailable = true;
        }
        else
        {
            gxText.setText("Gyroscope sensor not available!");
            isGyrometerSensorAvailable = false;
        }

        final String absPath = Objects.requireNonNull(this.getExternalFilesDir(null)).getAbsolutePath();

        recordButton = (Button)findViewById(R.id.rec);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    count = 0;
                    r = "";
                    record = true;
                    Log.d("Record time start", String.valueOf((int) new Timestamp(System.currentTimeMillis()).getTime()));
                }


            });

        fileButton = (Button)findViewById(R.id.FileButton);
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(writer != null){
                    writer.close();
                }
                if(filenameText.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(), "No file selected/given.", Toast.LENGTH_SHORT).show();
                    fileChoose = false;
                    fileTextDisplay.setText("No file selected/given.");
                }
                else{
                    Log.d("Sensor log", "Writing to " + absPath);

                    newFile = true;

                    File file = null;

                    if(!(file = new File(absPath, filenameText.getText().toString() + ".txt")).exists()) {
                        Toast.makeText(getBaseContext(), "New file created", Toast.LENGTH_SHORT).show();
                        try {
                            writer = new PrintWriter(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        r = "";
                        for (int i = 1; i <= 600; i++)
                            r = r + i + " ";
                        r = r + "Gesture";
                        writer.println(r);
                    }
                    else{
                        newFile = false;
                        try {
                            writer = new PrintWriter(new FileOutputStream(file, true));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getBaseContext(), "File exists", Toast.LENGTH_SHORT).show();
                    }
                    fileChoose = true;
                    fileTextDisplay.setText("Writing to " + filenameText.getText().toString() + ".txt");
                }
            }

        });
    }



    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = sensorEvent.values[0]; ay = sensorEvent.values[1]; az = sensorEvent.values[2];
            }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            gx = sensorEvent.values[0]; gy = sensorEvent.values[1]; gz = sensorEvent.values[2];
            }

        axText.setText(ax + " m/s2");
        ayText.setText(ay + " m/s2");
        azText.setText(az + " m/s2");
        gxText.setText("" + gx);
        gyText.setText("" + gy);
        gzText.setText("" + gz);


        if(record){
            count++;
            r = r + String.format("%f %f %f %f %f %f ", ax, ay, az, gx, gy, gz);
            if(count==100){
                Toast.makeText(getBaseContext(), "Cut!", Toast.LENGTH_SHORT).show();
                Log.d("Time", String.valueOf((int) new Timestamp(System.currentTimeMillis()).getTime()));
                record = false;
                r = r + gestureText.getText();
                if(fileChoose) {
                    if(newFile) {
                        writer.println(r);
                    }
                    else{
                        writer.append(r).append("\n");
                    }
                    Toast.makeText(getBaseContext(), "Written!", Toast.LENGTH_SHORT).show();
                }
                else {
                    r = "";
                    Toast.makeText(getBaseContext(), "Choose file to write!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isAccelerometerSensorAvailable)
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        if(isGyrometerSensorAvailable)
            sensorManager.registerListener(this, gyrSensor, SensorManager.SENSOR_DELAY_NORMAL);

        fileTextDisplay.setText("Choose file to write to.");
        writer = null;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isAccelerometerSensorAvailable)
            sensorManager.unregisterListener(this, accSensor);
        if(isGyrometerSensorAvailable)
            sensorManager.unregisterListener(this, gyrSensor);
        if(writer!=null)
            writer.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}