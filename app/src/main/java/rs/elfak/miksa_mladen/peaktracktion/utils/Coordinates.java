package rs.elfak.miksa_mladen.peaktracktion.utils;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates{
  public double latitude;
  public double longitude;

  public Coordinates() {
    // for .class
  }

  public Coordinates(double lat, double lng) {
    latitude = lat;
    longitude = lng;
  }

  public LatLng toGoogleCoords() {
    return new LatLng(latitude, longitude);
  }
}
