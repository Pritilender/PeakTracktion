package rs.elfak.miksa_mladen.peaktracktion.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.adapters.FriendListAdapter;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

public class FriendsFragment extends Fragment {
  private DatabaseReference mFriendRef;
  private FriendListAdapter mAdapter;

  private RecyclerView mRecView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_people, container, false);
    mRecView = (RecyclerView) v.findViewById(R.id.friends_friend_list);
    mRecView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    mRecView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    mRecView.setItemAnimator(new DefaultItemAnimator());

    mFriendRef = FirebaseDatabase.getInstance().getReference()
      .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends");
    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    mAdapter = new FriendListAdapter(this.getContext(), mRecView, mFriendRef);
    mRecView.setAdapter(mAdapter);
  }

  @Override
  public void onStop() {
    super.onStop();
    mAdapter.cleanupListener();
  }
}
