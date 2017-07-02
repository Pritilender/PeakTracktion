package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;
import rs.elfak.miksa_mladen.peaktracktion.models.User;
import rs.elfak.miksa_mladen.peaktracktion.providers.UserProvider;

public class ViewPlaceActivity extends AppCompatActivity implements View.OnClickListener {
  private TextView tvName;
  private TextView tvDescription;
  private TextView tvType;
  private TextView tvPoints;
  private TextView tvVisited;
  private TextView tvCreator;
  private ImageView ivImage;
  private ImageButton btnFav;
  private ProgressBar pbProgress;

  private Place mPlace;

  private boolean isFav = false;

  private DatabaseReference mPlaceRef, mUserRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_place);
    tvName = (TextView) findViewById(R.id.view_place_name);
    tvDescription = (TextView) findViewById(R.id.view_place_description);
    tvType = (TextView) findViewById(R.id.view_place_type);
    tvPoints = (TextView) findViewById(R.id.view_place_points);
    tvVisited = (TextView) findViewById(R.id.view_place_visited);
    tvCreator = (TextView) findViewById(R.id.view_place_creator_display_name);
    ivImage = (ImageView) findViewById(R.id.view_place_image);
    pbProgress = (ProgressBar) findViewById(R.id.view_place_progress);
    btnFav = (ImageButton) findViewById(R.id.view_place_favorite_btn);
    btnFav.setOnClickListener(this);
    setTitle("");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public boolean onSupportNavigateUp() {
    finish();
    return true;
  }

  @Override
  public void onStart() {
    super.onStart();
    String placeId = getIntent().getStringExtra("PLACE_ID");

    mPlaceRef = FirebaseDatabase.getInstance().getReference().child("places").child(placeId);
    mUserRef = FirebaseDatabase.getInstance().getReference().child("users");

    mPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Place place = dataSnapshot.getValue(Place.class);
        mPlace = place;
        mUserRef.child(place.creator).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            User creator = dataSnapshot.getValue(User.class);
            setupUi(creator);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            Log.e("VIEW PLACE", "Problem in fetching user.");
          }
        });
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e("VIEW PLACE", "Problem in fetching place.");
      }
    });
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.view_place_favorite_btn) {
      String currentUserId = UserProvider.getInstance().getUser().userId;
      if (isFav) {
        mUserRef.child(currentUserId).child("favoritePlaces").child(mPlace.placeId).removeValue();
        UserProvider.getInstance().getUser().favoritePlaces.remove(mPlace.placeId);
        btnFav.setImageResource(R.drawable.ic_favorite_border_black_24px);
      } else {
        mUserRef.child(currentUserId).child("favoritePlaces").child(mPlace.placeId).setValue(true);
        UserProvider.getInstance().getUser().favoritePlaces.put(mPlace.placeId, true);
        btnFav.setImageResource(R.drawable.ic_favorite_black_24px);
      }
      isFav = !isFav;
    }
  }

  private void setupUi(User creator) {
    tvName.setText(mPlace.name);
    tvDescription.setText(mPlace.description);
    tvType.setText(mPlace.type);
    tvPoints.setText("" + mPlace.points);
    tvVisited.setText("" + mPlace.timesVisited);
    tvCreator.setText(creator.displayName);
    Glide.with(this)
      .load(mPlace.imgURL)
      .apply(RequestOptions.centerInsideTransform())
      .into(ivImage);
    if (UserProvider.getInstance().getUser().favoritePlaces.containsKey(mPlace.placeId)) {
      isFav = true;
    } else {
      isFav = false;
      btnFav.setImageResource(R.drawable.ic_favorite_border_black_24px);
    }
    setTitle(mPlace.name);
    pbProgress.setVisibility(View.GONE);
  }
}
