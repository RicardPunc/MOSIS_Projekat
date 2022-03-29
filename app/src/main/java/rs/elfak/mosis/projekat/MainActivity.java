package rs.elfak.mosis.projekat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_ALL = 1;

    public Button logIn_btn;
    public EditText password_et;
    public EditText username_et;
    private static final String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserData.getInstance().isLoggedIn(this);

        username_et = findViewById(R.id.username_login_et);
        password_et = findViewById(R.id.password_login_et);

        logIn_btn = findViewById(R.id.logIn_btn);
        logIn_btn.setEnabled(true);

        TextView signup_tv = findViewById(R.id.textView_signup);

        logIn_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (hasPermissions(MainActivity.this, permissions)){
                    logIn();
                }
                else {
                        ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSIONS_ALL);
                }
            }
        });

        signup_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (hasPermissions(this, permissions)) {
            logIn();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        logIn_btn.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void logIn() {
        logIn_btn.setEnabled(false);
        String username = username_et.getText().toString();
        String password = password_et.getText().toString();
        UserData.getInstance().logIn(MainActivity.this, username, password);
    }



    public void logInSuccess(User user) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    public void logInFailure() {
        password_et.setError("Incorrect username or password.");
        logIn_btn.setEnabled(true);
    }

    public String getID() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}