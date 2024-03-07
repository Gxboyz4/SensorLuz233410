package com.example.practicasensorluzpropia233410;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView lightSensorValue;
    private RelativeLayout mainLayout;
    private MediaPlayer lightSoundPlayer;
    private MediaPlayer darkSoundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Obtener el sensor de luz
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Obtener referencia al TextView y al Layout principal
        lightSensorValue = findViewById(R.id.lightSensorValue);
        mainLayout = findViewById(R.id.mainLayout);

        // Inicializar reproductores de sonido
        lightSoundPlayer = MediaPlayer.create(this, R.raw.light_sound);
        darkSoundPlayer = MediaPlayer.create(this, R.raw.dark_sound);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registrar el Listener del sensor
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el Listener del sensor
        sensorManager.unregisterListener(this);

        // Liberar recursos de los reproductores de sonido
        if (lightSoundPlayer != null) {
            lightSoundPlayer.release();
        }
        if (darkSoundPlayer != null) {
            darkSoundPlayer.release();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Obtener el valor del sensor de luz
        float lightValue = event.values[0];

        // Reproducir sonido según el nivel de luz
        //Si el nivel de luz es mayor a 20, se reproduce un sonido (pajaros)
        //Si es el valor de luz es menor a 15 se reproduce un sonido (grillos)
        if (lightValue > 20 && !lightSoundPlayer.isPlaying()) {
            playSound(lightSoundPlayer);
            stopSound(darkSoundPlayer);
        } else if (lightValue < 15) {
            stopSound(lightSoundPlayer);
            playSound(darkSoundPlayer);
        }

        // Cambiar el color de fondo y el texto basado en el nivel de luz
        if (lightValue > 15) {
            mainLayout.setBackgroundColor(Color.WHITE);
            lightSensorValue.setTextColor(Color.BLACK);
            lightSensorValue.setText("Hay mucha luz afuera: \n"+
            "Valor del sensor de luz: "+lightValue);
        } else if (lightValue > 8 && lightValue <= 20) {
            mainLayout.setBackgroundColor(Color.GRAY);
            lightSensorValue.setTextColor(Color.BLACK);
            lightSensorValue.setText("Hay poca luz afuera \n"+
                    "Valor del sensor de luz: "+lightValue);
        } else {
            mainLayout.setBackgroundColor(Color.BLACK);
            lightSensorValue.setTextColor(Color.WHITE);
            lightSensorValue.setText("No hay luz afuera \n"+
                    "Valor del sensor de luz: "+lightValue);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sin lógica, se implementa por interfaz.
    }

    private void playSound(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }
    }
    private void stopSound(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
    }
}

