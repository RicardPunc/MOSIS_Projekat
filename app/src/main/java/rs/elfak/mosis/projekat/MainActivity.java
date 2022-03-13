package rs.elfak.mosis.projekat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public Button logIn_btn;
    public EditText password_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        EditText username_et = findViewById(R.id.username_login_et);
        password_et = findViewById(R.id.password_login_et);

        logIn_btn = findViewById(R.id.logIn_btn);
        logIn_btn.setEnabled(true);

        TextView signup_tv = findViewById(R.id.textView_signup);

        logIn_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                logIn_btn.setEnabled(false);
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();
                UserData.getInstance().logIn(MainActivity.this, username, password);
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

    public void logInSuccess(User user) {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void logInFailure() {
        password_et.setError("Incorrect username or password.");
        logIn_btn.setEnabled(true);
    }
}