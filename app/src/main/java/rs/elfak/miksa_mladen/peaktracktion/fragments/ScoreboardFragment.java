package rs.elfak.miksa_mladen.peaktracktion.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.adapters.FriendListAdapter;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScoreboardFragment extends Fragment {
  private ListView list;
  private ArrayList<User> people;
  private final String[] names = {
    "Mitar",
    "Boban",
    "Ipce",
    "Luis",
    "Jasar",
    "Zvonko",
    "Mile",
    "Bogdan",
    "Zdravko",
    "I naravno"
  };

  private final String[] surnames = {
    "Miric",
    "Zdravkovic",
    "Ahmedovski",
    "",
    "Ahmedovski",
    "Bogdan",
    "Kitic",
    "Zvonko",
    "Colic",
    "Dzej"
  };

  public ScoreboardFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    people = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      people.add(i, new User(names[i], surnames[i], ThreadLocalRandom.current().nextInt(0, 1000)));
    }
    sortPeople();
    FriendListAdapter adapter = new FriendListAdapter(this.getActivity(), people);

    View view = inflater.inflate(R.layout.fragment_people, container, false);
    list = (ListView) view.findViewById(R.id.list_view_friends);
    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
                              int position, long id) {
        // TODO Auto-generated method stub
        String selectedItem = names[position] + " " + surnames[position];
        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();
      }
    });
    return view;
  }

  private void sortPeople() {
    for (int i = 0; i < people.size() - 2; i++) {
      for (int j = i + 1; j < people.size() - 1; j++) {
        if (people.get(i).obtainedPoints < people.get(j).obtainedPoints) {
          Collections.swap(people, i, j);
        }
      }
    }
  }


}
