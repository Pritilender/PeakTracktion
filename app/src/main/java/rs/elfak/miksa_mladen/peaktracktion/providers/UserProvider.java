package rs.elfak.miksa_mladen.peaktracktion.providers;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

public class UserProvider {
  private User mUser;
  private static UserProvider mInstance = null;

  private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
  private StorageReference mImages = FirebaseStorage.getInstance().getReference();

  public static UserProvider getInstance() {
    if (mInstance == null) {
      mInstance = new UserProvider();
    }
    return mInstance;
  }

  private UserProvider() {
    // private constructor for singleton
  }

  public DatabaseReference getUser(String uid) {
    return mDatabase.child("users").child(uid);
  }

  public void setUser(User user) {
    mUser = user;
  }

  public UploadTask saveUserImage(Uri file) {
    StorageReference userProfileRef = mImages.child("images").child("users");
    return userProfileRef.putFile(file);
  }

  public void addNewUser(final String uid,
                         final String fullName,
                         final String email,
                         final String phone,
                         final String displayName,
                         final String imagePath) {
    Uri file = Uri.fromFile(new File(imagePath));
    saveUserImage(file)
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.e("IMAGE", e.getMessage());
        }
      })
      .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          Uri imageUrl = taskSnapshot.getDownloadUrl();
          mUser = new User(uid, fullName, displayName, email, phone, imageUrl.toString());
          mDatabase.child("users").child(mUser.userId).setValue(mUser);
        }
      });
  }

  public void updateUser(User updated) {
    mUser = updated;
    mDatabase.child("users").child(mUser.userId).setValue(mUser);
  }
}
