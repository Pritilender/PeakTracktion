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

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.adapters.UserListAdapter;

public class ScoreboardFragment extends Fragment {
  private Query mUsersRef;
  private UserListAdapter mAdapter;
  private RecyclerView mRecView;

  public ScoreboardFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_people, container, false);
    mRecView = (RecyclerView) v.findViewById(R.id.friends_friend_list);
    mRecView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    mRecView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    mRecView.setItemAnimator(new DefaultItemAnimator());

    mUsersRef = FirebaseDatabase.getInstance().getReference()
      .child("users").orderByChild("points");
    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    mAdapter = new UserListAdapter(this.getContext(), mRecView, mUsersRef, true);
    mRecView.setAdapter(mAdapter);
  }

  @Override
  public void onStop() {
    super.onStop();
    mAdapter.cleanupListener();
  }
}
