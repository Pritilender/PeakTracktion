package rs.elfak.miksa_mladen.peaktracktion.list_items;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by miksa on 25.6.17..
 */

public class Place {
  public String name;
  public String description;
  public String imgURL;
  public String creator;
  public String type;
  public LatLng coords;
  public int points;
  public int timesVisited;

  public Place(String name, String description, String imgURL) {
    this.name = name;
    this.description = description;
    this.imgURL = imgURL;
  }

  public Place(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
