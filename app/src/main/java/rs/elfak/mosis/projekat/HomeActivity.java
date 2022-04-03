package rs.elfak.mosis.projekat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.core.ComponentProvider;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.drawing.OsmBitmapShader;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.util.Base64;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_GPS = 99;
    public User user = null;
    public MapView map = null;
    public IMapController mapController = null;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public MyLocationNewOverlay myLocationOverlay;
    public LocationManager locationManager;
    public ItemizedIconOverlay locationsOverlay;
    public NavigationView nv;
    public Bitmap bitmap;
    private CountDownTimer timer;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");

        String photo = user.getPhoto_str();
        byte [] encodeByte=Base64.decode(photo, Base64.URL_SAFE) ;
        bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nv = findViewById(R.id.navigation_nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.S)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_account: {
                        Toast.makeText(HomeActivity.this, "Account", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.nav_show_all_users: {
                        CompoundButton switchView = (CompoundButton) item.getActionView();
                        item.setChecked(!item.isChecked());
                        switchView.setChecked(item.isChecked());

                        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                item.setChecked(isChecked);
                            }
                        });

                        if (item.isChecked()) {
                            showAllUsersOverlays();
                        }
                        else {
                            timer.cancel();
                            removeUsersOverlay();
                        }


                        break;
                    }
                    case R.id.nav_scoreboard: {
                        Intent i = new Intent(HomeActivity.this, ScoreboardActivity.class);
                        i.putExtra("user", user);
                        startActivity(i);

                        break;
                    }
                    case R.id.nav_logout: {
                        UserData.getInstance().logOut(user);
                        Intent i = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        break;
                    }

                }

                return false;
            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            Context ctx = getApplicationContext();
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
            map = findViewById(R.id.mapView);
            map.setMultiTouchControls(true);

            setMyLocationOverlay();
            UserData.getInstance().getAllLocations(HomeActivity.this);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //Build the alert dialog
                showGpsAlert();
            } else {
                GeoPoint location = myLocationOverlay.getMyLocation();
                if (location != null) {
                    user.setLocation( location );
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        GeoPoint geoPointLocation = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
                        user.setLocation(geoPointLocation);
                        UserData.getInstance().updateLocation(user);
                    }
                });
            }

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(HomeActivity.this, EditPlaceActivity.class);
                    i.putExtra("user", user);
                    i.putExtra("requestCode", "New Place");
                    startActivity(i);
                }
            });

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAllUsersOverlays() {

        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long l) {
                UserData.getInstance().getAllUsersLocations(HomeActivity.this);
            }

            @Override
            public void onFinish() {
                removeUsersOverlay();
            }
        }.start();
    }

    private void removeUsersOverlay() {
        for (Overlay overlay : map.getOverlays()) {
            if (overlay != locationsOverlay && overlay != myLocationOverlay) {
                map.getOverlays().remove(overlay);
            }
        }
    }

    private void showGpsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS not active");
        builder.setMessage("In order to use this app, we need you to turn your location on!");
        builder.setPositiveButton("Turn On Location", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_GPS);
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS ) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            GeoPoint geoPointLocation = new GeoPoint(location.getLatitude(), location.getLongitude(), location.getAltitude());
                            user.setLocation(geoPointLocation);
                            UserData.getInstance().updateLocation(user);
                        }
                    });
                }
            }
            else {
                showGpsAlert();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        NavigationMenuItemView itemView = findViewById(R.id.nav_account);
        @SuppressLint("RestrictedApi") MenuItem menuItem =  itemView.getItemData();
        LinearLayout layout = (LinearLayout) menuItem.getActionView();
        if (layout.getChildCount() < 2) {
            layout.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            CircleImageView circleImageView = new CircleImageView(this);
            circleImageView.setImageBitmap(bitmap);
            circleImageView.setForegroundGravity(Gravity.CENTER);
            layout.addView(circleImageView);
            TextView textView = new TextView(this);
            textView.setText(user.getUsername());
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20.0f);
            layout.addView(textView);
        }



        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setMyLocationOverlay() {
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(this.myLocationOverlay);
        mapController = map.getController();
        if (mapController != null) {
            mapController.setZoom(17.5);
            myLocationOverlay.enableFollowLocation();
        }
    }

    public void setLocationsOverlay(List<UserLocation> locations) {
        List<OverlayItem> list = new ArrayList<OverlayItem>();
        if (locations.size() != 0) {
            for (UserLocation location : locations) {
                OverlayItem item = new OverlayItem(location.getName(), location.getDescription(), location.getLocation());

                Drawable icon = getDrawable(org.osmdroid.library.R.drawable.marker_default);

                switch (location.getType()) {
                    case "Peak" : icon = getDrawable(R.drawable.ic_peak); break;
                    case "Resting Place" : icon = getDrawable(R.drawable.ic_resting_place); break;
                    case "Viewport" : icon = getDrawable(R.drawable.ic_viewport); break;
                    case "Springhead" : icon = getDrawable(R.drawable.ic_spring); break;
                    case "Danger" : icon = getDrawable(R.drawable.ic_warning); break;
                }
                item.setMarker(icon);
                list.add(item);
            }

            locationsOverlay = new ItemizedIconOverlay( list, new ItemizedIconOverlay.OnItemGestureListener() {
                @Override
                public boolean onItemSingleTapUp(int index, Object item) {
                    return false;
                }

                @Override
                public boolean onItemLongPress(int index, Object item) {
                    return false;
                }
            }, this);
            if (map.getOverlays().contains(locationsOverlay)) {
                map.getOverlays().remove(locationsOverlay);
            }

            map.getOverlays().add(locationsOverlay);

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void showUsers(List<User> users) {
        removeUsersOverlay();
        List<Marker> list = new ArrayList<>();
        if (users.size() != 0) {
            for (User user : users) {
                Marker marker = new Marker(map);
                marker.setPosition(user.getLocation());
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                marker.setTitle("User: " + user.getUsername() + "\nScore: " + user.getScore());
                //marker.setSubDescription("Score: " + user.getScore());

                String photo = user.getPhoto_str();
                byte [] encodeByte=Base64.decode(photo, Base64.URL_SAFE) ;
                Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                bitmap = getCroppedBitmap(bitmap);
                Drawable drawable = new BitmapDrawable(map.getResources(), bitmap);

                marker.setIcon(drawable);
                map.getOverlays().add(marker);
            }

        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onBackPressed() {
        return;
    }
}