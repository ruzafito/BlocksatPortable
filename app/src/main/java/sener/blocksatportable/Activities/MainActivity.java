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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    private static int requestActualState = 0;

    // Icons
    private ImageView i_signal;
    private ImageView i_gps;
    private ImageView i_ble;
    private ImageView i_battery;

    // Button
    private Button b_request;

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

    private static final String GPS_STATE_HEADER = "gss";
    private static final String GPS_POSITION_HEADER = "gsp";
    private static final String GPRS_STATE_HEADER = "grs";
    private static final String GPRS_SIGNAL_HEADER = "gri";
    private static final String CTC_STATE_HEADER = "cts";
    private static final String CTC_TRAIN_HEADER = "ctt";
    private static final String BATTERY_HEADER = "blv";
    private static final String INIT_HEADER = "ini";

    private static final int REQUEST_INITIAL_STATE = 0;
    private static final int REQUEST_WAITING_STATE = 1;
    private static final int REQUEST_GRANTED_STATE = 2;
    private static final int REQUEST_WORKING_STATE = 3;
    private static final int REQUEST_DENIED_STATE = 4;
    private static final int REQUEST_FINISH_STATE = 5;

    private int mState = UART_PROFILE_DISCONNECTED;
    private BluetoothDevice mDevice = null;
    private static boolean isConfig = false;

    public double coord_lat = 41.490272;
    public double coord_long = 2.107008;

    private int numOfTrains = 0;
    private String[] trains_id = new String[20];
    private double[] trains_lat = new double[20];
    private double[] trains_long = new double[20];

    String text = "";


    /**
     * Intent builder
     * @param activity Current activity
     * @return Intent
     */
    public final static Intent buildIntent(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public final static Intent buildIntentFromConfig(Activity activity, String ip, String port, String trainDistance, String sendFreq, String workTime) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("ip", ip);
        intent.putExtra("port", port);
        intent.putExtra("trainDistance", trainDistance);
        intent.putExtra("sendFreq", sendFreq);
        intent.putExtra("workTime", workTime);

        isConfig = true;

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
        i_signal = findViewById(R.id.gsm_signal);
        i_gps = findViewById(R.id.gps_signal);
        i_ble = findViewById(R.id.ble_signal);
        i_battery = findViewById(R.id.battery_level);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        b_request = findViewById(R.id.solicitar_inicio);

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
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        b_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = getStringValue(R.string.param_request_label) + getStringValue(R.string.parameter_separator);

                switch (requestActualState){
                    case REQUEST_INITIAL_STATE:
                        message = message + getStringValue(R.string.start_request_label);
                        sendData(message);
                        break;
                    case REQUEST_WAITING_STATE:
                        break;
                    case REQUEST_GRANTED_STATE:
                        break;
                    case REQUEST_WORKING_STATE:
                        message = message + getStringValue(R.string.stop_request_label);
                        sendData(message);
                        break;
                    case REQUEST_DENIED_STATE:
                        b_request.setText(R.string.solicitar);
                        b_request.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                        requestActualState = REQUEST_INITIAL_STATE;
                        break;
                    case REQUEST_FINISH_STATE:
                        b_request.setText(R.string.solicitar);
                        b_request.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                        requestActualState = REQUEST_INITIAL_STATE;
                        break;
                }
            }
        });



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
                            case R.id.nav_map:
                                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                                mapIntent.putExtra("coord_lat", coord_lat);
                                mapIntent.putExtra("coord_long", coord_long);
                                mapIntent.putExtra("numOfTrains", numOfTrains);
                                mapIntent.putExtra("trains_id", trains_id);
                                mapIntent.putExtra("trains_lat", trains_lat);
                                mapIntent.putExtra("trains_long", trains_long);
                                startActivity(mapIntent);
                                return false;
                            case R.id.nav_config:
                                startActivity(ConfigActivity.buildIntent(MainActivity.this));
                                return true;
                            case R.id.nav_info:
                                startActivity(InfoActivity.buildIntent(MainActivity.this));
                                return true;
                        }
                        return false;
                    }
                });
    }

    private void sendConfiguration(){
        if(isConfig){
            Intent intent = getIntent();
            String message = "";
            String ipCTC = intent.getStringExtra("ip");
            String portCTC = intent.getStringExtra("port");
            String trainDistance = intent.getStringExtra("trainDistance");
            String sendFreq = intent.getStringExtra("sendFreq");
            String workTime = intent.getStringExtra("workTime");

            message = getStringValue(R.string.param_config_label) + getStringValue(R.string.parameter_separator);
            message = message + ipCTC + getStringValue(R.string.values_separator);
            message = message + portCTC + getStringValue(R.string.values_separator);
            message = message + trainDistance + getStringValue(R.string.values_separator);
            message = message + sendFreq + getStringValue(R.string.values_separator);
            message = message + workTime;

            sendData(message);
            isConfig = false;
        }
    }



    /**
     * Call a function in BluetoothLeService to send a message
     * @param message The message to be sent
     */
    private void sendData(String message){
        byte[] value;
        message = getStringValue(R.string.message_tx_header) + getStringValue(R.string.header_separator) + message + getStringValue(R.string.end_separator);
        if (message.length() > 20){
            String message1 = message.substring(0,19);
            try {
                //send data to service
                value = message1.getBytes("UTF-8");
                mBluetoothLeService.writeRXCharacteristic(value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String message2 = message.substring(19);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                //send data to service
                value = message2.getBytes("UTF-8");
                mBluetoothLeService.writeRXCharacteristic(value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else{
            try {
                //send data to service
                value = message.getBytes("UTF-8");
                mBluetoothLeService.writeRXCharacteristic(value);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem mapItem = menu.getItem(1);
        mapItem.setVisible(true);
        mapItem.setChecked(false);
        MenuItem selectedItem = menu.getItem(0);
        selectedItem.setChecked(true);
        MenuItem configTiem = menu.getItem(2);
        configTiem.setChecked(false);
        MenuItem infoTiem = menu.getItem(3);
        infoTiem.setChecked(false);


        int s = mBluetoothAdapter.getState();
        // Check the state of the adapter to set the BLE icon initial state.
        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON ||
                mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF ||
                mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON){
            i_ble.setImageResource(R.drawable.ic_bluetooth_searching);

        }
        else if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
        {
            i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
        }

        switch (requestActualState){
            case REQUEST_INITIAL_STATE:
                b_request.setText(R.string.solicitar);
                b_request.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                break;
            case REQUEST_WAITING_STATE:
                b_request.setText(R.string.solicitando);
                b_request.setBackgroundColor(getResources().getColor(R.color.colorRemoteControl));
                break;
            case REQUEST_GRANTED_STATE:
                b_request.setText(R.string.concedido);
                b_request.setBackgroundColor(getResources().getColor(R.color.colorCommsLogs));
                break;
            case REQUEST_WORKING_STATE:
                b_request.setText(R.string.trabajando);
                b_request.setBackgroundColor(getResources().getColor(R.color.colorCommsLogs));
                break;
            case REQUEST_DENIED_STATE:
                b_request.setText(R.string.rechazado);
                b_request.setBackgroundColor(getResources().getColor(R.color.colorAlert));
                break;
            case REQUEST_FINISH_STATE:
                b_request.setText(R.string.finalizado);
                b_request.setBackgroundColor(getResources().getColor(R.color.colorGrey));
                break;
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


    /**
     * Sets the BLE icon to disabled and notify to the user.
     */
    private void bleOut(){
        i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
        i_ble.setBackgroundColor(getResources().getColor(R.color.colorAlert));
        showMessage(getStringValue(R.string.ble_disconnected));
    }

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

                String textToAdd = null;
                try {
                    textToAdd = new String(txValue, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                text = text + textToAdd;

                if (text.contains(getStringValue(R.string.end_separator)))
                {
                    text = text.replace(getStringValue(R.string.end_separator),"");
                    String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                    showMessage(currentDateTimeString + " -> " + text);

                    try {
                        processInputMessage(text);
                    } catch (Exception ignore) {
                        Log.e(TAG, ignore.toString() + ": " + text);
                    }
                    text = "";
                }

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
        i_ble.setImageResource(R.drawable.ic_bluetooth);
        if(received == null){
            showMessage(getStringValue(R.string.error_message_process));
            return;
        }

        // Split the header of the message.
        String[] labelParsed = received.split(getStringValue(R.string.header_separator));
        // Parse the parameter and values
        String[] parameterParsed = labelParsed[1].split(getStringValue(R.string.parameter_separator));
        String subsystem = parameterParsed[0];
        String[] values = parameterParsed[1].split(getStringValue(R.string.values_separator));

        if (labelParsed[0].equals(getStringValue(R.string.message_rx_header)) && labelParsed.length > 1){

            // Switch between the subsystems and launch actions if needed
            switch (subsystem){
                case INIT_HEADER:
                    if (values[0].equals("ok")){
                        initDeviceConnection();
                    }
                    break;
                case GPS_STATE_HEADER:
                    String gpsStateText = values[0];
                    checkGPSDeviceState(gpsStateText);
                    break;
                case GPS_POSITION_HEADER:
                    /// TODO: Something
                    String gpsCoordText = values[0];
                    getGPSCoord(gpsCoordText);
                    break;
                case BATTERY_HEADER:
                    String batteryText = values[0];
                    checkBatteryDevice(batteryText);
                    break;
                case GPRS_STATE_HEADER:
                    /// TODO: Something
                    break;
                case GPRS_SIGNAL_HEADER:
                    String gprsSignal = values[0];
                    checkGPRSDeviceSignal(gprsSignal);
                    break;
                case CTC_STATE_HEADER:
                    String ctcState = values[0];
                    updateCTCState(ctcState);
                    break;
                case CTC_TRAIN_HEADER:
                    String trainId = values[0];
                    String trainLat = values[1];
                    String trainLong = values[2];
                    String trainDistance = values[3];
                    addTrainToMap(trainId, trainLat, trainLong, trainDistance);
                    break;
            }
        }
        else if(!received.equals("ack"))
        {
            // Incorrect message format
            Log.e(TAG, "Incorrect message format received: " + received);
        }
    }

    private void addTrainToMap(String trainId, String tLat, String tLong, String tDistance){
        double trainLat = Double.valueOf(tLat);
        double trainLong = Double.valueOf(tLong);
        long trainDistance = Long.valueOf(tDistance);

        trains_id[numOfTrains] = trainId;
        trains_lat[numOfTrains] = trainLat;
        trains_long[numOfTrains] = trainLong;

        numOfTrains++;
    }

    /**
     * Set the double type global variables with the coordinates from device
     * @param gpsCoordText A string with the coordinates of the device
     */
    private void getGPSCoord (String gpsCoordText){
        String[] gpsCoord = gpsCoordText.split(",");
        coord_lat = Double.valueOf(gpsCoord[0]);
        coord_long = Double.valueOf(gpsCoord[1]);
    }

    /**
     * Set the BLE icon to connect and reply with an initOk
     */
    private void initDeviceConnection (){
        i_ble.setImageResource(R.drawable.ic_bluetooth);
        String replyMessage = getStringValue(R.string.param_init_label) + getStringValue(R.string.parameter_separator) + "ok";
        sendData(replyMessage);
        showMessage(getStringValue(R.string.device_bond_success));
        b_request.setText(R.string.solicitar);
        b_request.setBackgroundColor(getResources().getColor(R.color.colorGrey));
        requestActualState = REQUEST_INITIAL_STATE;
    }

    /**
     * Checks the GPRS state received from device and change its icon.
     * @param gpsStateText The text received from device with GPRS state
     */
    private void checkGPSDeviceState (String gpsStateText){
        if (gpsStateText.equals(getStringValue(R.string.gps_state_on))){
            i_gps.setImageResource(R.drawable.ic_gps_fixed);
        }
        else
        {
            i_gps.setImageResource(R.drawable.ic_gps_not_fixed);
        }
    }

    /**
     * Checks the battery level received from device and change its icon.
     * @param batteryText The text received from device with battery level
     */
    private void checkBatteryDevice (String batteryText){
        if (batteryText.equals(getStringValue(R.string.no_value_info))){
            i_battery.setImageResource(R.drawable.ic_battery_unknown);
        } else {
            int batteryLevel = 0;
            try {
                batteryLevel = Integer.parseInt(batteryText);
            } catch(NumberFormatException nfe) {
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
    }

    /**
     * Checks the GPRS signal level received from device and change its icon.
     * @param gprsSignal The text received from device with GPRS signal level
     */
    private void checkGPRSDeviceSignal (String gprsSignal){

        // https://foro.vodafone.es/t5/Android/intensidad-se%C3%B1al-dbm-y-asu/td-p/748990
        if (gprsSignal.equals(getStringValue(R.string.no_value_info))){
            i_signal.setImageResource(R.drawable.ic_signal_cellular_no_connected);
        } else {
            int gprsLevel = 0;
            try {
                gprsLevel = Integer.parseInt(gprsSignal);
            } catch(NumberFormatException nfe) {
                /// TODO: Handle parse error.
            }
            if(gprsLevel >= -75){
                i_signal.setImageResource(R.drawable.ic_signal_cellular_4);
            } else if (gprsLevel >= -85 && gprsLevel < - 75){
                i_signal.setImageResource(R.drawable.ic_signal_cellular_3);
            }else if (gprsLevel >= -97 && gprsLevel < - 85){
                i_signal.setImageResource(R.drawable.ic_signal_cellular_2);
            } else if (gprsLevel >= -110 && gprsLevel < -97){
                i_signal.setImageResource(R.drawable.ic_signal_cellular_0);
            } else if (gprsLevel < -110){
                i_signal.setImageResource(R.drawable.ic_signal_cellular_no_connected);
            }
        }
    }

    private void updateCTCState(String ctcState)
    {
        if(ctcState.equals(getStringValue(R.string.ctc_wait_state_label))){
            b_request.setText(R.string.solicitando);
            b_request.setBackgroundColor(getResources().getColor(R.color.colorRemoteControl));
            requestActualState = REQUEST_WAITING_STATE;
        }else if(ctcState.equals(getStringValue(R.string.ctc_granted_state_label))){
            b_request.setText(R.string.concedido);
            b_request.setBackgroundColor(getResources().getColor(R.color.colorCommsLogs));
            requestActualState = REQUEST_GRANTED_STATE;
        }else if(ctcState.equals(getStringValue(R.string.ctc_denied_state_label))){
            b_request.setText(R.string.rechazado);
            b_request.setBackgroundColor(getResources().getColor(R.color.colorAlert));
            requestActualState = REQUEST_DENIED_STATE;
        }else if(ctcState.equals(getStringValue(R.string.ctc_end_state_label))){
            b_request.setText(R.string.finalizado);
            b_request.setBackgroundColor(getResources().getColor(R.color.colorGrey));
            requestActualState = REQUEST_FINISH_STATE;
        }else if(ctcState.equals(getStringValue(R.string.ctc_work_state_label))){
            b_request.setText(R.string.trabajando);
            b_request.setBackgroundColor(getResources().getColor(R.color.colorCommsLogs));
            requestActualState = REQUEST_WORKING_STATE;
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
                if ((resultCode == RESULT_OK) && (data != null)) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    Log.d(TAG, "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mBluetoothLeService);
                    mBluetoothLeService.connect(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == RESULT_OK) {
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
            sendConfiguration();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
}
