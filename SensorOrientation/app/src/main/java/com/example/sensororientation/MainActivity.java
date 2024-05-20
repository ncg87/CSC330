package com.example.sensororientation;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//=================================================================================================
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener,
        SensorEventListener {
    //-------------------------------------------------------------------------------------------------
    private float[] magneticField = new float[3];
    private boolean magneticFieldAvailable;
    private float[] gravity = new float[3];
    private boolean gravityAvailable;
    private float[] orientation = new float[3];
    private boolean orientationAvailable;
    private float[] proximity = new float[1];
    private boolean proximityAvailable;
    private float[] light = new float[1];
    private boolean lightAvailable;

    private SensorManager sensorManager;
    private Display screen;
    private TextToSpeech screamer = null;
    //-------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        magneticFieldAvailable = startSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gravityAvailable = startSensor(Sensor.TYPE_ACCELEROMETER);
        orientationAvailable = magneticFieldAvailable && gravityAvailable;
        proximityAvailable = startSensor(Sensor.TYPE_PROXIMITY);
        lightAvailable = startSensor(Sensor.TYPE_LIGHT);

//----Start the TTS and pray it is ready in time for use
        screamer = new TextToSpeech(this,this);
    }
    //-------------------------------------------------------------------------------------------------
    private boolean startSensor(int sensorType) {

        if (sensorManager.getSensorList(sensorType).isEmpty()) {
            return(false);
        } else {
            sensorManager.registerListener(this,sensorManager.getDefaultSensor(sensorType),
                    SensorManager.SENSOR_DELAY_GAME);
            return(true);
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this,"Now you can talk",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,"You need to install TextToSpeech",Toast.LENGTH_LONG).show();
            finish();
        }
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        final String[] orientations  = {"Upright","Left side","Upside down","Right side"};

        super.onConfigurationChanged(newConfig);
        ((TextView)findViewById(R.id.orientation)).setText("The rotation is " +
                orientations[screen.getRotation()]);
    }
    //-------------------------------------------------------------------------------------------------
    public void onSensorChanged(SensorEvent event) {

        boolean gravityChanged,magneticFieldChanged,orientationChanged,proximityChanged,
                lightChanged;
        float[] rotation = new float[9];
        float[] inclination = new float[9];
        float[] newOrientation = new float[3];

        gravityChanged = magneticFieldChanged = orientationChanged = proximityChanged =
                lightChanged = false;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravityChanged = arrayCopyChangeTest(event.values,gravity,
                        Float.parseFloat(getResources().getString(R.string.minimum_gravity_change)));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticFieldChanged = arrayCopyChangeTest(event.values,magneticField,
                        Float.parseFloat(getResources().getString(R.string.minimum_magnetic_change)));
                break;
            case Sensor.TYPE_PROXIMITY:
                proximityChanged = arrayCopyChangeTest(event.values,proximity,
                        Float.parseFloat(getResources().getString(R.string.minimum_proximity_change)));
                break;
            case Sensor.TYPE_LIGHT:
                lightChanged = arrayCopyChangeTest(event.values,light,
                        Float.parseFloat(getResources().getString(R.string.minimum_light_change)));
                break;
            default:
                break;
        }

        if ((gravityChanged || magneticFieldChanged) &&
//----Transform from the device coordinate system to the world's coordinate system
                SensorManager.getRotationMatrix(rotation,inclination,gravity,magneticField)) {
            SensorManager.getOrientation(rotation,newOrientation);
            newOrientation[0] = (float)Math.toDegrees(newOrientation[0]);
            newOrientation[1] = (float)Math.toDegrees(newOrientation[1]);
            newOrientation[2] = (float)Math.toDegrees(newOrientation[2]);
            orientationChanged = arrayCopyChangeTest(newOrientation,orientation,Float.parseFloat(
                    getResources().getString(R.string.minimum_orientation_change)));
        }

        if (gravityChanged) {
            fallingTest(gravity);
        }
        if (orientationChanged || gravityChanged || proximityChanged || lightChanged) {
            updateSensorDisplay();
        }
    }
    //-------------------------------------------------------------------------------------------------
    private boolean arrayCopyChangeTest(float[] from,float[] to,float amountForChange) {

        int copyIndex;
        boolean changed = false;

        for (copyIndex=0;copyIndex < to.length;copyIndex++) {
            if (Math.abs(from[copyIndex] - to[copyIndex]) > amountForChange) {
                changed = true;
            }
        }
        if (changed) {
            for (copyIndex = 0; copyIndex < to.length; copyIndex++) {
                to[copyIndex] = from[copyIndex];
            }
        }
        return(changed);
    }
    //-------------------------------------------------------------------------------------------------
    private void updateSensorDisplay() {

        String sensorValues = "";
        final String format = "%5.1f";

        sensorValues += "Orientation\n";
        if (orientationAvailable) {
            sensorValues += "A " + String.format(format,orientation[0]) + ", " +
                    "P " + String.format(format,orientation[1]) + ", " + "R " + String.format(format,orientation[2]) +
                    "\n" + orientationToCompass(orientation) + "\n\n";
        } else {
            sensorValues += "Not available\n\n";
        }

        sensorValues += "Gravity\n";
        if (gravityAvailable) {
            sensorValues += "X " + String.format(format,gravity[0]) + "," +
                    "Y " + String.format(format,gravity[1]) + "," + "Z " + String.format(format,gravity[2]) + "\n\n";
        } else {
            sensorValues += "Not available\n\n";
        }

        sensorValues += "Proximity\n";
        if (proximityAvailable) {
            sensorValues += "P " + String.format(format, proximity[0]) + "\n\n";
        } else {
            sensorValues += "Not available\n\n";
        }

        sensorValues += "Light\n";
        if (lightAvailable) {
            sensorValues += "L " + String.format(format, light[0]) + "\n\n";
        } else {
            sensorValues += "Not available\n\n";
        }

        ((TextView)findViewById(R.id.sensors)).setText(sensorValues);
    }
    //-------------------------------------------------------------------------------------------------
    private String orientationToCompass(float[] orientation) {

        if (Math.abs(orientation[1]) >
                Float.parseFloat(getResources().getString(R.string.minimum_orientation_change)) ||
                Math.abs(orientation[2]) >
                        Float.parseFloat(getResources().getString(R.string.minimum_orientation_change))) {
            return("Phone is not flat - no meaningful compass bearing");
        } else {
            switch ((int)Math.round(orientation[0] / 45)) {
                case 0: return("Facing NORTH");
                case 1: return("Facing NORTH EAST");
                case 2: return("Facing EAST");
                case 3: return("Facing SOUTH EAST");
                case 4:
                case -4: return("Facing SOUTH");
                case -3: return("Facing SOUTH WEST");
                case -2: return("Facing WEST");
                case -1: return("Facing NORTH WEST");
                default: return("Confused by azimuth degree value " + orientation[0]);
            }
        }
    }
    //-------------------------------------------------------------------------------------------------
    public void onAccuracyChanged(Sensor sensor,int accuracy) {

        String sensorStatus;

        switch (accuracy) {
            case SensorManager.SENSOR_STATUS_NO_CONTACT:
                sensorStatus = "NO CONTACT";
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                sensorStatus = "UNRELIABLE";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                sensorStatus = "LOW";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                sensorStatus = "MEDUIM";
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                sensorStatus = "HIGH";
                break;
            default:
                sensorStatus = "UNKNOWN";
                break;
        }
        Toast.makeText(this,"The accuracy has changed for " + sensor.getName() + " to " +
                sensorStatus,Toast.LENGTH_SHORT).show();
    }
    //-------------------------------------------------------------------------------------------------
    private void fallingTest(float[] gravity) {

        if (gravity[1] > Float.parseFloat(getResources().getString(R.string.falling_gravity))) {
//            Toast.makeText(this,"Yikes, I'm falling",Toast.LENGTH_SHORT).show();
            screamer.speak("Heeeeelp",TextToSpeech.QUEUE_FLUSH,null,"HELP");
        }
    }
    //-------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {

        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
//-------------------------------------------------------------------------------------------------
}
//=================================================================================================