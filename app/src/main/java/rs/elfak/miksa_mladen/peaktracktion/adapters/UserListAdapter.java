package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;
import rs.elfak.miksa_mladen.peaktracktion.list_items.UserViewHolder;
import rs.elfak.miksa_mladen.peaktracktion.utils.UserItemClickListener;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {
  private Context mContext;
  private Query mUserQueryRef;
  private DatabaseReference mUserDatabaseRef;
  private ChildEventListener mChildEventListener;
  private Map<String, ValueEventListener> mUserValueEventListeners = new HashMap<>();
  private RecyclerView mParentRecView;
  private UserItemClickListener mUserItemClickListener;

  private ArrayList<String> userKeys = new ArrayList<>();
  private ArrayList<User> users = new ArrayList<>();
  private boolean mIsRanking;

  private int getTrueIndex(int ind) {
    return users.size() - (ind + 1);
  }

  public UserListAdapter(final Context context, RecyclerView rv, Query dbRef, boolean isRanking) {
    mContext = context;
    mUserQueryRef = dbRef;
    mParentRecView = rv;
    mIsRanking = isRanking;

    mUserItemClickListener = new UserItemClickListener(users, mParentRecView);

    mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

    ChildEventListener childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        final String friendId = dataSnapshot.getKey();

        ValueEventListener valueEventListener = new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            User friend = dataSnapshot.getValue(User.class);
            int friendIndex = userKeys.indexOf(friendId);

            if (friendIndex > -1) {
              users.set(friendIndex, friend);
              notifyItemChanged(getTrueIndex(friendIndex));
            } else {
              users.add(friend);
              userKeys.add(friendId);
              notifyItemInserted(getTrueIndex(users.size() - 1));
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            // Todo
          }
        };

        mUserDatabaseRef.child(friendId).addValueEventListener(valueEventListener);
        mUserValueEventListeners.put(friendId, valueEventListener);
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // TODO
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        int userIndex = userKeys.indexOf(key);
        if (userIndex > -1) {
          userKeys.remove(userIndex);
          users.remove(userIndex);
          notifyItemRemoved(userIndex);
          mUserDatabaseRef.child(key).removeEventListener(mUserValueEventListeners.get(key));
          mUserValueEventListeners.remove(key);
        } else {
          Log.w("FRIENDS", "Unknown user " + dataSnapshot.getKey());
        }
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Collections.sort(users, new Comparator<User>() {
          @Override
          public int compare(User o1, User o2) {
            return o1.points - o2.points;
          }
        });
        for (int i = 0; i < users.size(); i++) {
          userKeys.set(i, users.get(i).userId);
        }
        notifyDataSetChanged();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {
        Log.w("USERSADAPTER", "users cancelled", databaseError.toException());
        Toast.makeText(mContext, "Failed to load users.", Toast.LENGTH_SHORT).show();
      }
    };
    mUserQueryRef.addChildEventListener(childEventListener);
    mChildEventListener = childEventListener;
  }

  @Override
  public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View view = inflater.inflate(R.layout.list_item_friends, parent, false);
    view.setOnClickListener(mUserItemClickListener);
    return new UserViewHolder(view);
  }

  @Override
  public void onBindViewHolder(UserViewHolder holder, int position) {
    User user = users.get(getTrueIndex(position));

    holder.tvDisplayName.setText(user.displayName);
    holder.tvFullName.setText(user.fullName);
    holder.tvPoints.setText("" + user.points);
    Glide.with(mContext)
      .load(user.imgUrl)
      .apply(RequestOptions.circleCropTransform())
      .into(holder.ivUserImage);

    if (user.friends.containsKey(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
      holder.ivFriend.setVisibility(View.VISIBLE);
    }

    if (user.userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
      holder.tvDisplayName.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
      holder.tvFullName.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
    }

    if (!mIsRanking) {
      holder.tvRanking.setVisibility(View.GONE);
    } else {
      holder.tvRanking.setText("" + (position + 1));
    }
  }

  @Override
  public int getItemCount() {
    return users.size();
  }

  public void cleanupListener() {
    if (mChildEventListener != null) {
      mUserQueryRef.removeEventListener(mChildEventListener);
    }
    for (Map.Entry<String, ValueEventListener> entry: mUserValueEventListeners.entrySet()) {
      mUserDatabaseRef.child(entry.getKey()).removeEventListener(entry.getValue());
    }
  }
}
