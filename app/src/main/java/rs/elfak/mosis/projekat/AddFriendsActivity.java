package rs.elfak.mosis.projekat;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
//import com.google.firebase.auth.FirebaseUser
import java.util.*;
import java.util.function.Consumer;

public class AddFriendsActivity extends AppCompatActivity {

    public BluetoothService bluetoothService = new BluetoothService();

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice btDevicePaired;
    private UUID uuid = UUID.fromString("4f9257c9-61c2-4c75-8696-c7b4085316f7");

    private Integer REQUEST_ENABLE_BT = 1;
    private Integer REQUEST_DISCOVERABLE = 2;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<BluetoothDevice> devices;
    private HashMap<String, BluetoothDevice> devicesMap;

    private ArrayAdapter<String> searchArrayAdapter;
    private ArrayList<BluetoothDevice> searchDevices;
    private HashMap<String, BluetoothDevice> foundListMap;

    public ListView paired_device_list;
    public Button btn_visability;
    public Button select_device_refresh;
    public ProgressBar progressBar_discover;
    public ListView select_device_list;
    public User user = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        paired_device_list = (ListView) findViewById(R.id.paired_device_list);
        btn_visability = (Button) findViewById(R.id.btn_visability);
        select_device_refresh = (Button) findViewById((R.id.select_device_refresh));
        progressBar_discover = (ProgressBar) findViewById((R.id.progressBar_discover));
        select_device_list = (ListView) findViewById((R.id.select_device_list));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // No Bluetooth support
            finish();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityIfNeeded(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }

        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mArrayAdapter.clear();
        devices = new ArrayList<BluetoothDevice>();
        devicesMap = new HashMap<String, BluetoothDevice>();

        searchArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        searchArrayAdapter.clear();
        searchDevices = new ArrayList<BluetoothDevice>();
        foundListMap = new HashMap<String, BluetoothDevice>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver,filter);

        getPariedDevices();

        select_device_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter.startDiscovery();
            }
        });

        btn_visability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityIfNeeded(discoverableIntent,REQUEST_DISCOVERABLE);
            }
        });

        paired_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btDevicePaired = devices.get(i);
                showDialog(btDevicePaired,uuid);
            }
        });

        select_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBluetoothAdapter.cancelDiscovery();
                BluetoothDevice bluetoothDevice = searchDevices.get(i);
                bluetoothDevice.createBond();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPariedDevices() {
        devicesMap.clear();
        devices.clear();
        mArrayAdapter.clear();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.BLUETOOTH_CONNECT},REQUEST_ENABLE_BT);
        }
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        pairedDevices.forEach(new Consumer<BluetoothDevice>() {
            @SuppressLint("MissingPermission")
            @Override
            public void accept(BluetoothDevice bluetoothDevice) {
                devicesMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
                devices.add((bluetoothDevice));
                mArrayAdapter.add((bluetoothDevice.getName()!=null?bluetoothDevice.getName():"Unknown"));
            }
        });
        paired_device_list.setAdapter(mArrayAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_DISCOVERABLE) {
            bluetoothService.startThread();
        }
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                progressBar_discover.setVisibility(View.VISIBLE);
                foundListMap.clear();
                searchArrayAdapter.clear();
            }

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!searchDevices.contains(bluetoothDevice)) {
                    foundListMap.put(bluetoothDevice.getAddress(),bluetoothDevice);
                    searchDevices.add(bluetoothDevice);
                    searchArrayAdapter.add((bluetoothDevice.getName()!=null?bluetoothDevice.getName():"Unknown"));
                    select_device_list.setAdapter(searchArrayAdapter);
                }
            }

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progressBar_discover.setVisibility(View.GONE);
            }

            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    getPariedDevices();
                }
            }
        }
    };

    private void showDialog(BluetoothDevice bluetoothDevice, UUID uuid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Add a friend");
        builder.setTitle("Send request");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startBtConnection(bluetoothDevice,uuid);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void startBtConnection(BluetoothDevice bluetoothDevice, UUID uuid) {
        Bundle extras = getIntent().getExtras();
        user = (User) extras.getSerializable("user");
        bluetoothService.mConnectThread = bluetoothService.new ConnectThread(bluetoothDevice,user,uuid);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}