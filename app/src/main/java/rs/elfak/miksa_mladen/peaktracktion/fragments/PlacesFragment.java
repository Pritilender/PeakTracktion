package rs.elfak.miksa_mladen.peaktracktion.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.activities.EditPlaceActivity;
import rs.elfak.miksa_mladen.peaktracktion.adapters.PlacesAdapter;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;
import rs.elfak.miksa_mladen.peaktracktion.models.User;
import rs.elfak.miksa_mladen.peaktracktion.providers.UserProvider;

public class PlacesFragment extends Fragment {
  private DatabaseReference mPlacesRef;
  private PlacesAdapter mAdapter;
  private RecyclerView mRecView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_places, container, false);
    mRecView = (RecyclerView) v.findViewById(R.id.places_fragment_recycler_view);
    mRecView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecView.setItemAnimator(new DefaultItemAnimator());

    mPlacesRef = FirebaseDatabase.getInstance().getReference().child("places");

    FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.places_fragment_add_new_place);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), EditPlaceActivity.class);
        startActivity(intent);
      }
    });

    return v;
  }

  @Override
  public void onStart() {
    super.onStart();
    mAdapter = new PlacesAdapter(getContext(), mPlacesRef, UserProvider.getInstance().getUser());
    mRecView.setAdapter(mAdapter);
  }

  @Override
  public void onStop() {
    super.onStop();
    mAdapter.cleanupListeners();
  }

}
