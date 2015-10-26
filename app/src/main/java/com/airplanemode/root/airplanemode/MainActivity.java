package com.airplanemode.root.airplanemode;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    static final String STATUS_ON = "Airplane Mode: On";
    static final String STATUS_OFF = "Airplane Mode: Off";
    static final String TURN_ON = "Turn ON";
    static final String TURN_OFF = "Turn OFF";

    private String[] states;
    private Button inicio;
    private NumberPicker tiempo;
    private NumberPicker unidades;
    private TextView cronometro;

    Thread thread;
    int milisegundos, contador;

    boolean enable, x, running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initComponents();

        x = false;
        enable = true;
        try {
            setMobileDataEnabled(this, enable);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    private void initComponents(){

        running = false;

        states = getResources().getStringArray(R.array.unidades);

        cronometro = (TextView) findViewById(R.id.tiempo_textView);
        cronometro.setText(R.string.intervalo);

        tiempo = (NumberPicker) findViewById(R.id.tiempo_numberPicker);
        tiempo.setMinValue(1);
        tiempo.setMaxValue(60);
        tiempo.setWrapSelectorWheel(true);

        unidades = (NumberPicker) findViewById(R.id.numberPicker);
        unidades.setMinValue(1);
        unidades.setMaxValue(states.length);
        unidades.setDisplayedValues(states);
        unidades.setWrapSelectorWheel(true);

        WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        inicio = (Button) findViewById(R.id.button);
        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    running = false;
                    inicio.setText(R.string.iniciar);
                    thread.interrupt();
                } else {
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (x) {
                                enable = !enable;
                                try {
                                    setMobileDataEnabled(MainActivity.this, enable);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                } catch (NoSuchFieldException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    Thread.sleep(milisegundos, 0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });


                    running = true;
                    if (states[unidades.getValue() - 1].equalsIgnoreCase("minutos")) {
                        milisegundos = tiempo.getValue() * 60 * 1000;
                    } else {
                        milisegundos = tiempo.getValue() * 1000;
                    }
                    inicio.setText(R.string.detener);
                    x = true;
                    thread.start();
                }
            }
        });
    }


    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        final ConnectivityManager conman = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class iConnectivityManagerClass =     Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
    }
}
