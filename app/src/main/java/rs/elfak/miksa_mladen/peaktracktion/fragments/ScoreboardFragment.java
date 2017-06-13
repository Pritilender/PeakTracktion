package rs.elfak.miksa_mladen.peaktracktion.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.elfak.miksa_mladen.peaktracktion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment {


  public ScoreboardFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_scoreboard, container, false);
  }

}
