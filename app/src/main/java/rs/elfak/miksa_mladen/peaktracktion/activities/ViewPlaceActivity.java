package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;

/**
 * Created by turboMladen on 30.6.17..
 */

public class ViewPlaceActivity extends AppCompatActivity implements View.OnClickListener {
  private Place place;
  private TextView name;
  private TextView description;
  private TextView type;
  private ImageView image;
  private ImageButton btnFavorite;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    name = (TextView) findViewById(R.id.view_place_name);
    description = (TextView) findViewById(R.id.view_place_description);
    type = (TextView) findViewById(R.id.view_place_type);
    image = (ImageView) findViewById(R.id.view_place_image);
    btnFavorite = (ImageButton) findViewById(R.id.activity_view_place_favorite_btn);
    btnFavorite.setOnClickListener(this);
    if (isFavorite()) {
//TODO add to favorites list
      btnFavorite.setImageResource(R.drawable.ic_favorite_black_24px);
    } else {
//TODO remove from favorites list
      btnFavorite.setImageResource(R.drawable.ic_favorite_border_black_24px);
    }

  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.activity_view_place_favorite_btn) {

    }
  }

  public void addToFavorites() {
    btnFavorite.setImageResource(R.drawable.ic_favorite_black_24px);
  }

  public void removeFromFavorites() {
    btnFavorite.setImageResource(R.drawable.ic_favorite_border_black_24px);
  }

  public boolean isFavorite() {
    return false;
  }
}
