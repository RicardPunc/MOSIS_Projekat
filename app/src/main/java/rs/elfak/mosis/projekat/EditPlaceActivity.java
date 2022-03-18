package rs.elfak.mosis.projekat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.osmdroid.util.GeoPoint;

import java.util.jar.Manifest;

public class EditPlaceActivity extends AppCompatActivity {


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);

        DecimalFormat df = new DecimalFormat("0.00000");

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        setTitle(intent.getStringExtra("requestCode"));

        EditText lat_et = findViewById(R.id.edit_place_lat_et);
        EditText lon_et = findViewById(R.id.edit_place_lon_et);
        EditText alt_et = findViewById(R.id.edit_place_alt_et);
        EditText name_et = findViewById(R.id.edit_place_name_et);
        EditText desc_et = findViewById(R.id.edit_place_desc_et);
        Button cancel_btn = findViewById(R.id.edit_place_cancel_btn);
        Button finish_btn = findViewById(R.id.edit_place_finish_btn);

        GeoPoint location = user.getLocation();

        System.out.println(location.getLatitude());

        lat_et.setText(String.valueOf(df.format(location.getLatitude())));
        lon_et.setText(String.valueOf(df.format(location.getLongitude())));
        alt_et.setText(String.valueOf(df.format(location.getAltitude())));

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = name_et.getText().toString();
                String desc = desc_et.getText().toString();
                UserData.getInstance().addLocation(new UserLocation(name, desc, location));
            }
        });
    }
}