package sener.blocksatportable.Adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sener.blocksatportable.R;

/**
 * <h1>Adapter: LeDeviceAdapter</h1>
 * <p>
 * Adapter for holding devices found through scanning.
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-03-10
 */
public class LeDeviceListAdapter extends BaseAdapter {

    /**
     * An ArrayList containing BluetoothDevices
     */
    private ArrayList<BluetoothDevice> mLeDevices;

    /**
     * A LayoutInflater containing the views of the devices
     */
    private LayoutInflater mInflater;

    /**
     * Contains the Image Resource ID of the device list
     */
    private int deviceListLayout;

    /**
     * Set the class fields with the values given
     * @param context Context where the adapter will be used
     * @param layout Device list layout where the adapter will be used
     */
    public LeDeviceListAdapter(Context context, int layout) {
        super();

        this.deviceListLayout = layout;

        mLeDevices = new ArrayList<BluetoothDevice>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Add a device to the Adapter Array List of Bluetooth devices.
     * @param device The devices to be added
     */
    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    /**
     * Get a device from the Adapter Array List of Bluetooth devices.
     * @param position The position of gotten device
     * @return The device in the position.
     */
    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    /**
     * Clear all the devices inside the Adapter Array List of Bluetooth devices
     */
    public void clear() {
        mLeDevices.clear();
    }

    /**
     * Get the Adapter Array List of Bluetooth devices size
     * @return The number of devices in the list
     */
    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    /**
     * Get an device object from the Adapter Array List of Bluetooth devices.
     * @param i The position of gotten device
     * @return The device object in the position.
     */
    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflater.inflate(deviceListLayout, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText(R.string.unknown_device);
        viewHolder.deviceAddress.setText(device.getAddress());

        return view;
    }
}

/**
 * Class to hold the view
 */
class ViewHolder {
    TextView deviceName;
    TextView deviceAddress;
}