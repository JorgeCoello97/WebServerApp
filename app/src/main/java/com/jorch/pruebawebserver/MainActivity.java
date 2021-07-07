package com.jorch.pruebawebserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private WebServer webServer;
    private boolean power_flag = false;
    
    @BindView(R.id.sustituloTextView)
    TextView sustituloTextView;

    @BindView(R.id.secundarioCardView)
    CardView secundarioCardView;

    @BindView(R.id.direccionIPTextView)
    TextView direccionIPTextView;

    @BindView(R.id.webserverButton)
    Button webserverButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webServer != null)
            webServer.stop();
    }

    @SuppressLint("ResourceAsColor")
    @OnClick(R.id.webserverButton)
    public void powerWebServer(View view){
        power_flag = !power_flag;

        if (power_flag){
            try {
                webServer = new WebServer(8000, this);
                webServer.start();

                sustituloTextView.setText(R.string.powerOn);

                webserverButton.setText(getString(R.string.powerOffBUTTON));
                webserverButton.setBackgroundTintList(ColorStateList.valueOf(R.color.powerOff_RojoA_Negro));

                secundarioCardView.setVisibility(View.VISIBLE);

                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                final String formatedIpAddress = String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff));

                StringBuilder direccionServidor = new StringBuilder();
                direccionServidor.append(formatedIpAddress);
                direccionServidor.append(":");
                direccionServidor.append(webServer.getListeningPort());

                direccionIPTextView.setText(direccionServidor);
            }catch (IOException e){
                e.printStackTrace();

            }
        }else{
            if(webServer != null)
                webServer.stop();
            sustituloTextView.setText(R.string.powerOff);

            webserverButton.setText(getString(R.string.powerOnBUTTON));
            webserverButton.setBackgroundTintList(ColorStateList.valueOf(R.color.powerOn_NegroARojo));
            secundarioCardView.setVisibility(View.INVISIBLE);
            direccionIPTextView.setText("");
        }
    }
}