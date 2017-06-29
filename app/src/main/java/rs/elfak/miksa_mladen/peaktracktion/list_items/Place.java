package rs.elfak.miksa_mladen.peaktracktion.list_items;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class Place {
  public String placeId;
  public String name;
  public String description;
  public String imgURL = "https://unsplash.it/200/?random";
  public String creator;
  public String type;
  public LatLng coords;
  public int points;
  public int timesVisited;

  public Place() {

  }

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
