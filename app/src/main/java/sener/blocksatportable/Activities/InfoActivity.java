package sener.blocksatportable.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import sener.blocksatportable.Others.Auxiliary;
import sener.blocksatportable.R;

/**
 * <h1>Activity: InfoActivity</h1>
 * <p>
 * Activity that shows the information of the App
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-02-25
 */

public class InfoActivity extends AppCompatActivity {

    /**
     * Drawer Layout for the lateral menu
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Intent builder
     * @param activity Current activity
     * @return Intent
     */
    public static final Intent buildIntent(Activity activity) {
        Intent intent = new Intent(activity, InfoActivity.class);
        if (activity.getClass() != MainActivity.class) {
            activity.finish();
        }
        return intent;
    }

    /**
     * This is the override of the onCreate method from the activity
     * this method is executed when the activity is created and
     * links the activity with the layout, initialize the variables
     * of the class, starts the listeners of the buttons
     * and load the gestures of the library
     * @param savedInstanceState The previous state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Links the item of the layout with its field.
        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Listener of the info_contact button
        findViewById(R.id.info_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the contact is clicked, starts an activity to send an email to the contact.
                try {
                    startActivity(Intent.createChooser(Auxiliary.sendEmail("sergiodelhorno@gmail.com", "[INFO] ", ""), "Enviar email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(InfoActivity.this,
                            "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
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

        // Links the navigation view of the layout to the local field navigationView
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        MenuItem mapItem = menu.getItem(1);
        mapItem.setVisible(false);

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
                                startActivity(MainActivity.buildIntent(InfoActivity.this));
                                return true;
                            case R.id.nav_config_devi:
                                startActivity(ConfigDevActivity.buildIntent(InfoActivity.this));
                                return true;
                            case R.id.nav_config_app:
                                startActivity(ConfigAppActivity.buildIntent(InfoActivity.this));
                                return true;
                            case R.id.nav_info:
                                return true;
                        }
                        return true;
                    }
                });
    }

}
