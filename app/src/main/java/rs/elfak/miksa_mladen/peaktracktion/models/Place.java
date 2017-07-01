package rs.elfak.miksa_mladen.peaktracktion.models;

import rs.elfak.miksa_mladen.peaktracktion.utils.Coordinates;

public class Place {
  public String placeId;
  public String name;
  public String description;
  public String imgURL = "https://unsplash.it/200/?random";
  public String creator;
  public String type;
  public Coordinates coords;
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
