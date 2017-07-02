package rs.elfak.miksa_mladen.peaktracktion.fragments;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.activities.ShowDevicesActivity;
import rs.elfak.miksa_mladen.peaktracktion.adapters.FriendListAdapter;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {

  private final static int REQUEST_ENABLE_BT = 1;
  private ListView list;
  private FloatingActionButton fabAddFriend;
  private BluetoothAdapter mBluetoothAdapter;
  private final String[] names = {
    "Mitar Miric",
    "Boban Zdravkovic",
    "Ipce Ahmedovski",
    "Luis",
    "Jasar Ahmedovski",
    "Zvonko Demirovic",
    "Mile Kitic",
    "Bogdan Bogdanovic",
    "Zdravko Colic",
    "Dzej Ramadadadanovski"
  };

  private final int[] points = {
    18, 333, 486, 42, 156, 489, 687, 654, 123, 45, 12, 4, 666,
  };

  public PeopleFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    ArrayList<User> friends = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      friends.add(i, new User(names[i]));
    }
    FriendListAdapter adapter = new FriendListAdapter(this.getActivity(), friends);

    View view = inflater.inflate(R.layout.fragment_people, container, false);
    list = (ListView) view.findViewById(R.id.list_view_friends);
    fabAddFriend = (FloatingActionButton) view.findViewById(R.id.fab_add_friend);
    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
                              int position, long id) {
        String selectedItem = names[position];
        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
      }
    });
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter.isEnabled()) {
    } else {
    }

    fabAddFriend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mBluetoothAdapter.isEnabled()) {
          mBluetoothAdapter.startDiscovery();
          selectServer();
        } else {
          activateBluetooth();
        }
      }
    });
    return view;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
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

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main, menu);
  }

  private void activateBluetooth() {
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      Toast.makeText(getActivity(), "No Bluetooth support!", Toast.LENGTH_SHORT).show();
    }
    if (!mBluetoothAdapter.isEnabled()) {
      Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
    }
  }

  private void selectServer() {
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    ArrayList<String> pairedDeviceStrings = new ArrayList<>();
    if (pairedDevices.size() > 0) {
      for (BluetoothDevice device : pairedDevices) {
        pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
      }
    }
    Intent showDevicesIntent = new Intent(getActivity(), ShowDevicesActivity.class);
    showDevicesIntent.putStringArrayListExtra("devices", pairedDeviceStrings);
    startActivity(showDevicesIntent);
  }
}
