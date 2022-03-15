package rs.elfak.mosis.projekat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Bundle;
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

        GeoPoint location = user.getLocation();

        System.out.println(location.getLatitude());

        lat_et.setText(String.valueOf(df.format(location.getLatitude())));
        lon_et.setText(String.valueOf(df.format(location.getLatitude())));
        alt_et.setText(String.valueOf(df.format(location.getLatitude())));
    }
}