package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;
import rs.elfak.miksa_mladen.peaktracktion.list_items.UserViewHolder;
import rs.elfak.miksa_mladen.peaktracktion.utils.FriendItemClickListener;

public class FriendListAdapter extends RecyclerView.Adapter<UserViewHolder> {
  private Context mContext;
  private DatabaseReference mFriendsDatabaseRef;
  private DatabaseReference mUserDatabaseRef;
  private ChildEventListener mChildEventListener;
  private Map<String, ValueEventListener> mFriendValueEventListeners = new HashMap<>();
  private RecyclerView mParentRecView;
  private FriendItemClickListener mFriendItemClickListener;

  private ArrayList<String> friendsKeys = new ArrayList<>();
  private ArrayList<User> friends = new ArrayList<>();

  public FriendListAdapter(final Context context, RecyclerView rv, DatabaseReference dbRef) {
    mContext = context;
    mFriendsDatabaseRef = dbRef;
    mParentRecView = rv;

    mFriendItemClickListener = new FriendItemClickListener(friends, mParentRecView);

    mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

    ChildEventListener childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final String friendId = dataSnapshot.getKey();

        ValueEventListener valueEventListener = new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            User friend = dataSnapshot.getValue(User.class);
            int friendIndex = friendsKeys.indexOf(friendId);

            if (friendIndex > -1) {
              friends.set(friendIndex, friend);
              notifyItemChanged(friendIndex);
            } else {
              friends.add(friend);
              friendsKeys.add(friendId);
              notifyItemInserted(friends.size() - 1);
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            // Todo
          }
        };

        mUserDatabaseRef.child(friendId).addValueEventListener(valueEventListener);
        mFriendValueEventListeners.put(friendId, valueEventListener);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // TODO
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        int friendIndex = friendsKeys.indexOf(key);
        if (friendIndex > -1) {
          friendsKeys.remove(friendIndex);
          friends.remove(friendIndex);
          notifyItemRemoved(friendIndex);
          mUserDatabaseRef.child(key).removeEventListener(mFriendValueEventListeners.get(key));
          mFriendValueEventListeners.remove(key);
        } else {
          Log.w("FRIENDS", "Unknown user " + dataSnapshot.getKey());
        }
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // TODO
        Toast.makeText(mContext, "Child moved: " + dataSnapshot.getKey(), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.w("FRIENDS", "friends cancelled", databaseError.toException());
        Toast.makeText(mContext, "Failed to load friends.", Toast.LENGTH_SHORT).show();
      }
    };
    mFriendsDatabaseRef.addChildEventListener(childEventListener);
    mChildEventListener = childEventListener;
  }

  @Override
  public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View view = inflater.inflate(R.layout.list_item_friends, parent, false);
    view.setOnClickListener(mFriendItemClickListener);
    return new UserViewHolder(view);
  }

  @Override
  public void onBindViewHolder(UserViewHolder holder, int position) {
    User friend = friends.get(position);

    holder.tvDisplayName.setText(friend.displayName);
    holder.tvFullName.setText(friend.fullName);
    holder.tvPoints.setText("" + friend.points);
    Glide.with(mContext)
      .load(friend.imgUrl)
      .apply(RequestOptions.circleCropTransform())
      .into(holder.ivUserImage);
  }

  @Override
  public int getItemCount() {
    return friends.size();
  }

  public void cleanupListener() {
    if (mChildEventListener != null) {
      mFriendsDatabaseRef.removeEventListener(mChildEventListener);
    }
    for (Map.Entry<String, ValueEventListener> entry: mFriendValueEventListeners.entrySet()) {
      mUserDatabaseRef.child(entry.getKey()).removeEventListener(entry.getValue());
    }
  }
}
