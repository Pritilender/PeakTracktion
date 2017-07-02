package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.adapters.BluetoothListAdapter;

/**
 * Created by TurboMladen on 1.7.17..
 */

public class ShowDevicesActivity extends AppCompatActivity {
  private ListView list;
  private static final int REQUEST_ENABLE_BT = 1;
  private BluetoothAdapter mBluetoothAdapter;
  private BluetoothListAdapter adapter;
  private ArrayList<String> devicesList = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    // search for more devices
    mBluetoothAdapter.startDiscovery();
    // user selects one device
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    devicesList = getIntent().getExtras().getStringArrayList("devices");

    setContentView(R.layout.activity_bluetooth);
    list = (ListView) findViewById(R.id.activity_bluetooth_list_view);

    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    registerReceiver(mReceiver, filter);

    adapter = new BluetoothListAdapter(this, devicesList);
    list.setAdapter(adapter);
  }

  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (BluetoothDevice.ACTION_FOUND.equals(action)) {
        // Discovery has found a device. Get the BluetoothDevice
        // object and its info from the Intent.
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address

        devicesList.add(deviceName + "\n" + deviceHardwareAddress);
        resetAdapter();
      }
    }
  };

  public void resetAdapter() {
    adapter = new BluetoothListAdapter(this, devicesList);
    Set<String> hs = new HashSet<>();
    hs.addAll(devicesList);
    devicesList.clear();
    devicesList.addAll(hs);
    list.setAdapter(adapter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Don't forget to unregister the ACTION_FOUND receiver.
    unregisterReceiver(mReceiver);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_bar_bluetooth_visibility_button:
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivity(discoverableIntent);
        break;
      case R.id.action_bar_bluetooth_button:
        if (!mBluetoothAdapter.isEnabled()) {
          activateBluetooth();
          item.setIcon(R.drawable.ic_bluetooth_black_24dp);
        } else {
          mBluetoothAdapter.disable();
          item.setIcon(R.drawable.ic_bluetooth_disabled_black_24dp);
        }
        break;
    }
    return true;
  }


  private void activateBluetooth() {
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      Toast.makeText(this, "No Bluetooth support!", Toast.LENGTH_SHORT).show();
    }
    if (!mBluetoothAdapter.isEnabled()) {
      Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
    }
  }
}
