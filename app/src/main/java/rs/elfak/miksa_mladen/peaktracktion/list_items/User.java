package rs.elfak.miksa_mladen.peaktracktion.list_items;

import java.util.Date;
import java.util.List;

/**
 * Created by miksa on 27.6.17..
 */

public class User {
  public String firstName;
  public String lastName;
  public Date dateOfBirth;
  public List<User> friendlist;
  public int obtainedPoints;
  public String imgURL = "https://unsplash.it/200/?random";
  public List<Place> createdPlaces;
  public List<Place> visitedPlaces;

  public User(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
  public User(String firstName, String lastName, int obtainedPoints) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.obtainedPoints = obtainedPoints;
  }

}
