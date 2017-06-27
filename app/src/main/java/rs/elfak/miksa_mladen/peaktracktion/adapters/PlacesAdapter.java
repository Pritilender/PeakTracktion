package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.Place;

/**
 * Created by miksa on 25.6.17..
 */

public class PlacesAdapter extends ArrayAdapter<Place> {

  private final ArrayList<Place> places;

  public PlacesAdapter(Context context, ArrayList<Place> places) {
    super(context, 0, places);

    this.places = places;
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    Place place = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_places, parent, false);
    }
    TextView textView_name = (TextView) convertView.findViewById(R.id.tvName);
    TextView textView_desc = (TextView) convertView.findViewById(R.id.tvDesc);
    ImageView imageView_image = (ImageView) convertView.findViewById(R.id.imageView);

    textView_name.setText(place.name);
    textView_name.setTypeface(null, Typeface.BOLD);
    textView_desc.setText(place.description);
    imageView_image.setImageResource(R.mipmap.ic_launcher);

    return convertView;
  }
}
