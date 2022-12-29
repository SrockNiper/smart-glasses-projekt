package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private int layoutId;
    private List<BluetoothDevice> devices;

    public DeviceAdapter(Pridani_zarizeni context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        this.devices = new ArrayList<>();
    }

    public void addDevice(BluetoothDevice device) {
        devices.add(device);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutId, parent, false);

            holder = new ViewHolder();
            holder.deviceNameTextView = convertView.findViewById(R.id.device_name);
            holder.connectButton = convertView.findViewById(R.id.connect_button);
            holder.connectButton.setOnClickListener((View.OnClickListener) context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = devices.get(position);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle missing permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 0);
            return null;
        }

        holder.deviceNameTextView.setText(device.getName());
        holder.connectButton.setTag(device);

        return convertView;
    }

    static class ViewHolder {
        TextView deviceNameTextView;
        Button connectButton;
    }
}
