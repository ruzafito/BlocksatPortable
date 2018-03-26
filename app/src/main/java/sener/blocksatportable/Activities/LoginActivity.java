package sener.blocksatportable.Activities;

import android.app.Activity;
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

    public static final Intent buildIntent(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        return intent;
    }

    @InjectView(R.id.input_user) EditText _userText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

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
                    startActivity(Intent.createChooser(Auxiliary.sendEmail("sergiodelhorno@gmail.com", "[SIGN UP] ", "Solicitud de usuario de acceso para el operario: [Introducir nombre, número identificativo y datos de contacto]"), "Enviar email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this,
                            "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
                }

//                Toast.makeText(LoginActivity.this,"TBD",
//                        Toast.LENGTH_SHORT).show();
            }
        });

    }

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
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);

        } else {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            //onLoginSuccess();
                             onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
    }


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

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

//        if (user.isEmpty()) {
//            _userText.setError("introduzca un usuario correcto");
//            valid = false;
//        } else {
//            _userText.setError(null);
//        }

//        if (password.isEmpty() || password.length() < 3) {
//            _passwordText.setError("la contraseña debe contener más de 3 caracteres");
//            valid = false;
//        } else {
//            _passwordText.setError(null);
//        }

        return valid;
    }
}