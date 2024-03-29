package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Nastavovaci_stranka extends AppCompatActivity {
    Date currentTime = Calendar.getInstance().getTime();
    Button testbt;
    Button casbt;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progress;
    String adresa = null;
    String cas = "";
    private SimpleDateFormat dateFormat;
    private Calendar calendar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nastavovaci_stranka);
        testbt = findViewById(R.id.button1);
        casbt = findViewById(R.id.tlacitko);

        dateFormat = new SimpleDateFormat("HH:mm:ss");
        Intent intent = getIntent();
        String jmenod = intent.getExtras().getString("jmeno");

        Toast.makeText(this, jmenod, Toast.LENGTH_LONG).show();
        if (jmenod != null) {
            int delka = jmenod.length();
            adresa = jmenod.substring(delka - 17);
            Toast.makeText(this, adresa, Toast.LENGTH_LONG).show();

        }

        testbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("Komunikace navazana");

            }
        });
        casbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10){
                    cas += "0";
                }
                cas += String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":";
                if (Calendar.getInstance().get(Calendar.MINUTE) < 10){
                    cas+= "0";
                }
                cas += String.valueOf(Calendar.getInstance().get(Calendar.MINUTE)) + ":";
                if (Calendar.getInstance().get(Calendar.SECOND) < 10){
                    cas += "0";
                }

                cas += String.valueOf(Calendar.getInstance().get(Calendar.SECOND));
                sendSignal(cas);
                cas="";
            }
        });
        new ConnectBT().execute();
        //NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        /*if(n.isNotificationPolicyAccessGranted()) {
        }else{
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        }*/
        //LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
    private void sendSignal ( String number ) {
        if ( btSocket != null ) {
            try {
                btSocket.getOutputStream().write(number.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }else{
            msg("Nepodařilo se odeslat");
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Nastavovaci_stranka.this, "Připojování", "Zkouška spojení");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(adresa);
                    if (ActivityCompat.checkSelfPermission(Nastavovaci_stranka.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Připojení selhalo");
                finish();
            } else {
                msg("Připojeno");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }
    private BroadcastReceiver onNotice= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra("package");
            String titleData = intent.getStringExtra("title");
            String textData = intent.getStringExtra("text");
            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml(packageName +"<br><b>" + titleData + " : </b>" + textData));
            tr.addView(textview);
        }
    };
}