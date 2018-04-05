package sener.blocksatportable.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

import sener.blocksatportable.Communications.BluetoothLeService;
import sener.blocksatportable.R;

/**
 * <h1>Activity: MainActivity</h1>
 * <p>
 * Activity that shows and manage tha main screen of the app.
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-02-24
 */

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    /**
     * Drawer Layout for the lateral menu
     */
    private DrawerLayout mDrawerLayout;

    // Bluetooth Adapter to manage the events and states of the BLE connection
    private BluetoothAdapter mBluetoothAdapter;

    // Icons
    private ImageView i_signal;
    private ImageView i_gps;
    private ImageView i_ble;
    private ImageView i_battery;

    /// TODO: Eliminar codigo. Solo pruebas
    private boolean cambioIcono = false;

    /**
     * BluetoohLeService which manage the BLE Service of the app.
     */
    private BluetoothLeService mBluetoothLeService;

    // MainActivity constants
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int UART_PROFILE_READY = 10;
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;

    private static final String GPS_STATE_HEADER = "gpssta";
    private static final String GPS_POSITION_HEADER = "gpspos";
    private static final String GPRS_STATE_HEADER = "gprsst";
    private static final String GPRS_SIGNAL_HEADER = "gprssi";
    private static final String BATTERY_HEADER = "batter";

    private int mState = UART_PROFILE_DISCONNECTED;
    private BluetoothDevice mDevice = null;


    /**
     * Intent builder
     * @param activity Current activity
     * @return Intent
     */
    public static final Intent buildIntent(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    /**
     * This is the override of the onCreate method from the activity.
     * This method is executed when the activity is created and
     * links the activity with the layout, initialize the variables
     * of the class, starts the listeners of the buttons
     * and loads and manages the icons.
     * @param savedInstanceState The previous state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link the items of the layout to its fields.
        i_signal = (ImageView) findViewById(R.id.gsm_signal);
        i_gps = (ImageView) findViewById(R.id.gps_signal);
        i_ble = (ImageView) findViewById(R.id.ble_signal);
        i_battery = (ImageView) findViewById(R.id.battery_level);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Set default images for the icons.
        i_signal.setImageResource(R.drawable.ic_signal_cellular_off);
        i_gps.setImageResource(R.drawable.ic_gps_off);
        i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
        i_battery.setImageResource(R.drawable.ic_battery_unknown);

        initBLEService();

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        /// TODO: Eliminar codigo. Solo pruebas
        findViewById(R.id.solicitar_inicio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Prueba desde App";
                if(cambioIcono){
                    i_signal.setImageResource(R.drawable.ic_signal_cellular_0);
                    cambioIcono = false;
                    message = "Boton 1";
                } else{
                    i_signal.setImageResource(R.drawable.ic_signal_cellular_4);
                    cambioIcono = true;
                    message = "Y boton 2";
                }

                sendData(message);

            }
        });


//        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        // Listener of the button_menu to open the lateral menu
        findViewById(R.id.button_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Registration of the receiver for an action state changed in the Bluetooth Adapter
        IntentFilter filterState = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastStateReceiver, filterState);

        // Registration of the receiver for actions state changed in the Bluetooth Device
        IntentFilter filterConnection = new IntentFilter();
        filterConnection.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filterConnection.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(mBroadcastConnectionReceiver, filterConnection);

        // Links the navigation view of the layout to the local field navigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // start the activity selected in the menu
                        switch (menuItem.getItemId()) {
                            case R.id.nav_blocksat_portable:
                                return true;
                            case R.id.nav_config_devi:
                                startActivity(ConfigDevActivity.buildIntent(MainActivity.this));
                                return true;
                            case R.id.nav_config_app:
                                startActivity(ConfigAppActivity.buildIntent(MainActivity.this));
                                return true;
                            case R.id.nav_info:
                                startActivity(InfoActivity.buildIntent(MainActivity.this));
                                return true;
                        }
                        return true;
                    }
                });
    }

    /**
     * Call a function in BluetoothLeService to send a message
     * @param message The message to be sent
     */
    private void sendData(String message){
        byte[] value;
        try {
            //send data to service
            value = message.getBytes("UTF-8");
            mBluetoothLeService.writeRXCharacteristic(value);
            //Update the log with time stamp
            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
            /// TODO: Use the log
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Function executed when the activity is resumed.
     * Resume the activity, check states of the systems
     * and update the icons if needed.
     */
    @Override
    public void onResume(){
        super.onResume();

//        IntentFilter filterData = new IntentFilter();
//        filterData.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filterData.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        registerReceiver(mBroadcastDataReceiver, filterData);

        // Check the state of the adapter to set the BLE icon initial state.
        /// TODO: consider revising.
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON ||
                mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF ||
                mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON){
            i_ble.setImageResource(R.drawable.ic_bluetooth_searching);
        }
        else if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
        {
            i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
        }

        Log.d(TAG, "onResume");
        if (!mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "onResume - BT not enabled yet");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * Implements the Broadcast Receiver of the Bluetooth Adapter state.
     */
    private final BroadcastReceiver mBroadcastStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if(state == BluetoothAdapter.STATE_OFF)
                {
                    i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
                }
                else
                {
                    i_ble.setImageResource(R.drawable.ic_bluetooth_searching);
                }
            }
        }
    };

    /**
     * Implements the Broadcast Receiver of the Bluetooth Device connection state.
     */
    private final BroadcastReceiver mBroadcastConnectionReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (action != null) {
                switch (action){
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        i_ble.setImageResource(R.drawable.ic_bluetooth_searching);
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
                        bleOut();
                        break;
                }
            }
        }
    };

//    private final BroadcastReceiver mBroadcastDataReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//            switch (action){
//                case BluetoothDevice.ACTION_ACL_CONNECTED:
//                    i_ble.setImageResource(R.drawable.ic_bluetooth_searching);
//                    break;
//                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                    bleOut("ATENCIÓN: Dispositivo BLE desconectado");
//                    break;
//            }
//        }
//    };

    /**
     * Sets the BLE icon to disabled and notify to the user.
     */
    private void bleOut(){
        i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
        i_ble.setBackgroundColor(getResources().getColor(R.color.colorAlert));
        showMessage(getStringValue(R.string.ble_disconnected));
    }


//    private Handler mHandler = new Handler() {
//        @Override
//
//        //Handler events that received from UART service
//        public void handleMessage(Message msg) {
//
//        }
//    };

    /**
     * Implements the Broadcast Receiver of the UART GATT Service.
     */
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) return;

            //final Intent mIntent = intent;
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_CONNECTED)) {
                /// TODO: Check if needed
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_CONNECT_MSG");
                        mState = UART_PROFILE_CONNECTED;
                    }
                });
            }
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        mState = UART_PROFILE_DISCONNECTED;
                        // If the profile is disconnected then close the BLE Service
                        mBluetoothLeService.close();
                    }
                });
            }
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                // If UART GATT Service is discovered then enable BLE Service to receive TX notifications
                mBluetoothLeService.enableTXNotification();
            }
            //*********************//
            if (action.equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                // Data received from UART Service, so the BLE device sent a message, let's process it

                final byte[] txValue = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);

                String text = null;
                try {
                    text = new String(txValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //String text = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                showMessage(currentDateTimeString + " -> " + text);

                processInputMessage(text);
            }
            //*********************//
            if (action.equals(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)){
                showMessage("Device doesn't support UART. Disconnecting");
                mBluetoothLeService.disconnect();
            }
        }
    };

    /**
     * Parse a received message from BLE device according to communications protocol of the system
     * and then process the message and launch actions if needed.
     * @param received The received message from BLE device.
     */
    private void processInputMessage(String received){
        if(received == null){
            showMessage(getStringValue(R.string.error_message_process));
            return;
        }

        // Split the header of the message.
        String[] labelParsed = received.split(getStringValue(R.string.header_separator));

        // Check the message is an init message or a values message.
        if(received.equals(getStringValue(R.string.init_rx))){
            // If is an init message set the BLE icon to connect and reply with an initOk
            i_ble.setImageResource(R.drawable.ic_bluetooth);
            sendData(getStringValue(R.string.init_response));
            showMessage(getStringValue(R.string.device_bond_success));
        } else if (labelParsed[0].equals(getStringValue(R.string.message_header)) && labelParsed.length > 1){
            // If is a value message, parse the parameter and values
            String[] parameterParsed = labelParsed[1].split(getStringValue(R.string.parameter_separator));
            String subsystem = parameterParsed[0];
            String[] values = parameterParsed[1].split(getStringValue(R.string.values_separator));

            // Switch between the subsystems and launch actions if needed
            switch (subsystem){
                case GPS_STATE_HEADER:
                    String gpsStateText = values[0];
                    if (gpsStateText.equals(getStringValue(R.string.gps_state_on))){
                        i_gps.setImageResource(R.drawable.ic_gps_fixed);
                    }
                    else
                    {
                        i_gps.setImageResource(R.drawable.ic_gps_not_fixed);
                    }
                    break;
                case GPS_POSITION_HEADER:
                    break;
                case BATTERY_HEADER:
                    String batteryText = values[0];
                    if (batteryText.equals(getStringValue(R.string.no_battery_info))){
                        i_battery.setImageResource(R.drawable.ic_battery_unknown);
                    } else {
                        int batteryLevel = 0;
                        try {
                            batteryLevel = Integer.parseInt(batteryText);
                        } catch(NumberFormatException nfe) {
                            /// TODO: Handle parse error.
                        }
                        if(batteryLevel >= 90){
                            i_battery.setImageResource(R.drawable.ic_battery_full);
                        } else if (batteryLevel >= 60){
                            i_battery.setImageResource(R.drawable.ic_battery_60);
                        } else if (batteryLevel < 60 & batteryLevel >= 30){
                            i_battery.setImageResource(R.drawable.ic_battery_30);
                        } else if (batteryLevel < 30){
                            i_battery.setImageResource(R.drawable.ic_battery_alert);
                        }
                    }
                    break;
                case GPRS_STATE_HEADER:
                    break;
                case GPRS_SIGNAL_HEADER:
                    String gprsSignal = values[0];
                    if(!gprsSignal.equals("0"))
                    {
                        /// TODO
                        i_signal.setImageResource(R.drawable.ic_signal_cellular_no_connected);
                    }
                    break;
            }
        }
        else if(!received.equals("ack"))
        {
            // Incorrect message format
            /// TODO: Something
            Log.e(TAG, "Incorrect message format received: " + received);
        }
    }

    /**
     * Initialization of the BLE service and register the broadcast receiver of the UART Service
     */
    private void initBLEService() {
        Intent bindIntent = new Intent(this, BluetoothLeService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        // Registration of the receiver for actions changes in UART GATT Service
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    /**
     * Add the GATT actions required for receive the update from GATT service
     * @return The intent Filter to be registered
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }

    /**
     * Gets the string value from the resources library for a given resource ID
     * @param resource The resource ID of the String
     * @return The string value of the resources library
     */
    public String getStringValue(int resource){
        return getResources().getString(resource);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastConnectionReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastStateReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        unbindService(mServiceConnection);
        mBluetoothLeService.stopSelf();
        mBluetoothLeService = null;
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        try {
            unregisterReceiver(mBroadcastConnectionReceiver);
            unregisterReceiver(mBroadcastStateReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        try {
            unregisterReceiver(mBroadcastConnectionReceiver);
            unregisterReceiver(mBroadcastStateReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mBluetoothLeService);
                    mBluetoothLeService.connect(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    showMessage("Bluetooth has turned on ");
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    showMessage("Problem in BT Turning ON ");
                    finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    /**
     * Show a message in the App through a Toast.
     * @param msg The message to be showed.
     */
    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

}
