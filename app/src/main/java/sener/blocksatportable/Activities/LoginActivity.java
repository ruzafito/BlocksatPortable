package sener.blocksatportable.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import sener.blocksatportable.Communications.DeviceScanActivity;
import sener.blocksatportable.Others.Auxiliary;
import sener.blocksatportable.R;

/**
 * <h1>Activity: LoginActivity</h1>
 * <p>
 * Activity that shows and manage the user login screen.
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-02-25
 * @credits https://sourcey.com/beautiful-android-login-and-signup-screens-with-material-design/
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

//    /**
//     * Intent builder
//     * @param activity Current activity
//     * @return Intent
//     */
//    public static Intent buildIntent(Activity activity) {
//        return new Intent(activity, LoginActivity.class);
//    }

    @InjectView(R.id.input_user) EditText _userText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    /**
     * This is the override of the onCreate method from the activity
     * this method is executed when the activity is created and
     * links the activity with the layout, initialize the variables
     * of the class, starts the listeners of the buttons
     * and load the gestures of the library
     * @param savedInstanceState The previous state of the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
//                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
//                startActivityForResult(intent, REQUEST_SIGNUP);

                try {
                    startActivity(Intent.createChooser(
                            Auxiliary.sendEmail(getResources().getString(R.string.contact),
                                    getResources().getString(R.string.sign_up_label),
                                    getResources().getString(R.string.user_request_text)),
                            getResources().getString(R.string.send_email_title)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this,
                            getResources().getString(R.string.no_mail_clients), Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(LoginActivity.this,"TBD",
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Log the user into the App.
     * Checks the authentication of the user and if is correct launch the app.
     */
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        if ((user.equals("block") && password.equals("sat")) || (user.equals("") && password.equals(""))){
            Intent intent = new Intent(LoginActivity.this, DeviceScanActivity.class);
            startActivity(intent);
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                            progressDialog.dismiss();
                        }
                    }, 3000);

        } else {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                             onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    /**
     * Sets the animation when Login success
     */
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    /**
     * Sets the animation when Login failed
     */
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    /**
     * Checks if the login input data is valid and notifies to the user.
     * @return a boolean indicating if data input is valid or not
     */
    public boolean validate() {
        boolean valid = true;

        String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

        if (user.isEmpty()) {
            _userText.setError(getResources().getString(R.string.incorrect_user));
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (password.isEmpty() || password.length() < 3) {
            _passwordText.setError(getResources().getString(R.string.incorrect_password));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}