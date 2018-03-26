package sener.blocksatportable.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import sener.blocksatportable.R;

/**
 * <h1>Activity: ConfigDevActivity</h1>
 * <p>
 * Activity that shows the information of the App
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-02-25
 */

public class ConfigDevActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    public static final Intent buildIntent(Activity activity) {
        Intent intent = new Intent(activity, ConfigDevActivity.class);
        if (activity.getClass() != MainActivity.class) {
            activity.finish();
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_dev);

        mDrawerLayout = findViewById(R.id.drawer_layout);

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
                                startActivity(MainActivity.buildIntent(ConfigDevActivity.this));
                                return true;
                            case R.id.nav_config_devi:
                                return true;
                            case R.id.nav_config_app:
                                startActivity(ConfigAppActivity.buildIntent(ConfigDevActivity.this));
                                return true;
                            case R.id.nav_info:
                                startActivity(InfoActivity.buildIntent(ConfigDevActivity.this));
                                return true;
                        }

                        return true;
                    }
                });

    }

}