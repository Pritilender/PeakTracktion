package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
    TextView tvName = (TextView) convertView.findViewById(R.id.text_name_place);
    TextView tvDescription = (TextView) convertView.findViewById(R.id.text_desc_place);
    ImageView imageViewPlaceImage = (ImageView) convertView.findViewById(R.id.image_place);

    tvName.setText(place.name);
    tvDescription.setText(place.description);
    Glide.with(getContext())
      .load(place.imgURL)
      .apply(RequestOptions.circleCropTransform())
      .into(imageViewPlaceImage);

    return convertView;
  }
}
