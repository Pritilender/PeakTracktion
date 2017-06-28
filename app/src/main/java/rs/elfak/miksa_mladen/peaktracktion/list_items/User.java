package rs.elfak.miksa_mladen.peaktracktion.list_items;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class User {
  public String userId;
  public String name;
  public String email;
  public LatLng location = new LatLng(0, 0);;
  public int points = 0;
  public ArrayList<String> createdPlaces = new ArrayList<>();
  public ArrayList<String> visitedPlaces = new ArrayList<>();
  public ArrayList<String> friends = new ArrayList<>();

  public User(String name) {
    this.name = name;
    this.email = name + "@friend.com";
  }

  public User(String id, String name, String email) {
    this.userId = id;
    this.name = name;
    this.email = email;
  }
}
