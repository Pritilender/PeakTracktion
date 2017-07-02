package rs.elfak.miksa_mladen.peaktracktion.list_items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import rs.elfak.miksa_mladen.peaktracktion.R;

public class PlaceViewHolder extends RecyclerView.ViewHolder {
  public ImageView ivPlaceImage;
  public ImageView ivFavorite;
  public TextView tvPlaceName;
  public TextView tvPlaceType;
  public TextView tvPlaceDescription;

  public PlaceViewHolder(View itemView) {
    super(itemView);
    ivPlaceImage = (ImageView) itemView.findViewById(R.id.item_places_image);
    ivFavorite = (ImageView) itemView.findViewById(R.id.item_places_favorite);
    tvPlaceName = (TextView) itemView.findViewById(R.id.item_places_name);
    tvPlaceType = (TextView) itemView.findViewById(R.id.item_places_type);
    tvPlaceDescription = (TextView) itemView.findViewById(R.id.item_places_description);
    ivFavorite.setVisibility(View.INVISIBLE);
  }
}
