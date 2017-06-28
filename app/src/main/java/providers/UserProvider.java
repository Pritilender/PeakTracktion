package providers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rs.elfak.miksa_mladen.peaktracktion.list_items.Place;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

public class UserProvider {
  private User mUser;
  private static UserProvider mInstance = null;

  private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

  public static UserProvider getInstance() {
    if (mInstance == null) {
      mInstance = new UserProvider();
    }
    return mInstance;
  }

  private UserProvider() {
    // private constructor for singleton
  }

  public void addNewUser(String uid, String name, String email) {
    mUser = new User(uid, name, email);
    mDatabase.child("users").child(mUser.userId).setValue(mUser);
  }

  public void createPlace(Place place) {
    place.placeId = mDatabase.child("users").child(mUser.userId).child("createdPlaces")
      .push()
      .getKey();
    mDatabase.child("places").child(place.placeId)
      .setValue(place);
    mUser.createdPlaces.add(place.placeId);
  }

  public void visitPlace(Place place) {
    mUser.visitedPlaces.add(place.placeId);
  }
}
