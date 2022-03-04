package rs.elfak.mosis.projekat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText username_et = findViewById(R.id.username_login_et);
        EditText password_et = findViewById(R.id.password_login_et);


        Button logIn_btn = findViewById(R.id.logIn_btn);

        TextView signup_tv = findViewById(R.id.textView_signup);

        logIn_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String username = username_et.getText().toString();
                String password = password_et.getText().toString();
                if (UserData.getInstance().logIn(username, password)) {
                    Toast.makeText(MainActivity.this, "LOGIN SUCCESS", Toast.LENGTH_SHORT).show();
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
}