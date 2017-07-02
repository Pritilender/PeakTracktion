package rs.elfak.miksa_mladen.peaktracktion.fragments;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Set;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.activities.ShowDevicesActivity;
import rs.elfak.miksa_mladen.peaktracktion.adapters.UserListAdapter;

public class FriendsFragment extends Fragment {
  private final static int REQUEST_ENABLE_BT = 1;
  private DatabaseReference mFriendRef;
  private UserListAdapter mAdapter;
  private BluetoothAdapter mBluetoothAdapter;
  private RecyclerView mRecView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_people, container, false);
    mRecView = (RecyclerView) v.findViewById(R.id.friends_friend_list);
    mRecView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    mRecView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    mRecView.setItemAnimator(new DefaultItemAnimator());

    mFriendRef = FirebaseDatabase.getInstance().getReference()
      .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");

    FloatingActionButton fabAddFriend = (FloatingActionButton) v.findViewById(R.id.fab_add_friend);
    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    mAdapter = new UserListAdapter(this.getContext(), mRecView, mFriendRef, false);
    mRecView.setAdapter(mAdapter);
  }

  @Override
  public void onStop() {
    super.onStop();
    mAdapter.cleanupListener();
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

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main, menu);
  }
}
