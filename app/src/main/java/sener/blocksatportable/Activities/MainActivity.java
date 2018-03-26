package sener.blocksatportable.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

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

    // Drawer Layout for the lateral menu
    private DrawerLayout mDrawerLayout;

    // Bluetooth Adapter to manage the events and states of the BLE connection
    private BluetoothAdapter mBluetoothAdapter;

    // Icons
    private ImageView i_signal;
    private ImageView i_gps;
    private ImageView i_ble;
    private ImageView i_battery;

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

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Open menu
        findViewById(R.id.button_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

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

    @Override
    public void onResume(){
        super.onResume();

        if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
            i_ble.setImageResource(R.drawable.ic_bluetooth);
        }
        else if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF)
        {
            i_ble.setImageResource(R.drawable.ic_bluetooth_disabled);
        }
        else if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF || mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON)
        {
            i_ble.setImageResource(R.drawable.ic_bluetooth_searching);
        }


    }
}
