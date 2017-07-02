package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.PlaceViewHolder;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;
import rs.elfak.miksa_mladen.peaktracktion.models.User;
import rs.elfak.miksa_mladen.peaktracktion.providers.UserProvider;

public class PlacesAdapter extends RecyclerView.Adapter<PlaceViewHolder> {
  private ArrayList<Place> mPlaces = new ArrayList<>();
  private ArrayList<String> mPlaceKeys = new ArrayList<>();
  private Context mContext;

  private DatabaseReference mPlacesDatabaseRef;
  private User mUser;
  private ChildEventListener mChildEventListener;

  public PlacesAdapter(final Context context, DatabaseReference dbRef, User u) {
    mContext = context;
    mPlacesDatabaseRef = dbRef;
    mUser = u;

    ChildEventListener childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Place place = dataSnapshot.getValue(Place.class);
        String placeKey = dataSnapshot.getKey();
        mPlaces.add(place);
        mPlaceKeys.add(placeKey);
        notifyItemInserted(mPlaces.size() - 1);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Place place = dataSnapshot.getValue(Place.class);
        String placeKey = dataSnapshot.getKey();
        int index = mPlaceKeys.indexOf(placeKey);
        mPlaces.set(index, place);
        notifyItemChanged(index);
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        String placeKey = dataSnapshot.getKey();
        int index = mPlaceKeys.indexOf(placeKey);
        mPlaces.remove(index);
        notifyItemRemoved(index);
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.w("PLACES ADAPTER", "Child moved " + dataSnapshot.getKey());
        Toast.makeText(mContext, "Child moved " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.e("PLACES ADAPTER", "Places cancelled", databaseError.toException());
      }
    };

    mPlacesDatabaseRef.addChildEventListener(childEventListener);
    mChildEventListener = childEventListener;
  }

  public void cleanupListeners() {
    if (mChildEventListener != null) {
      mPlacesDatabaseRef.removeEventListener(mChildEventListener);
    }
  }

  @Override
  public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_places, parent, false);
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        int ind = ((RecyclerView)v.getParent()).getChildLayoutPosition(v);
        Toast.makeText(mContext, "Clicked on place " + mPlaces.get(ind).name, Toast.LENGTH_SHORT).show();
      }
    });
    return new PlaceViewHolder(view);
  }

  @Override
  public void onBindViewHolder(PlaceViewHolder holder, int position) {
    Place place = mPlaces.get(position);

    Glide.with(mContext)
      .load(place.imgURL)
      .apply(RequestOptions.centerInsideTransform())
      .into(holder.ivPlaceImage);

    if (mUser.favoritePlaces.containsKey(place.placeId)) {
      holder.ivFavorite.setVisibility(View.VISIBLE);
    }

    holder.tvPlaceName.setText(place.name);
    holder.tvPlaceDescription.setText(place.description);
    holder.tvPlaceType.setText(place.type);
  }

  @Override
  public int getItemCount() {
    return mPlaces.size();
  }
}
