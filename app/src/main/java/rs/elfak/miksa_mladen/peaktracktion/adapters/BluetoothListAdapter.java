package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.R;

/**
 * Created by miksa on 1.7.17..
 */

public class BluetoothListAdapter extends ArrayAdapter<String> {
  public BluetoothListAdapter(Context context, ArrayList<String> resource) {
    super(context, 0, resource);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    String device = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_bluetooth_device, parent, false);
    }

    TextView name = (TextView) convertView.findViewById(R.id.list_item_bluetooth_name);
    TextView address = (TextView) convertView.findViewById(R.id.list_item_bluetooth_address);
    String[] lines = device.split(System.getProperty("line.separator"));

    name.setText(lines[0]);
    address.setText(lines[1]);

    return convertView;
  }
}
