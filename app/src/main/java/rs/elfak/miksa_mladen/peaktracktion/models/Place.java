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

  public Place(String name, String description, String imgURL, String creator, Coordinates coordinates, String type) {
    this.name = name;
    this.description = description;
    this.imgURL = imgURL;
    this.coords = coordinates;
    this.creator = creator;
    this.timesVisited = 1;
    this.points = 100;
    this.type = type;
  }

  public Place() {
  }
}
