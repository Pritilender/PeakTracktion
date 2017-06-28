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

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.adapters.FriendListAdapter;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {

  private ListView list;
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

  private final int[] points = {
    18, 333, 486, 42, 156, 489, 687, 654, 123, 45, 12, 4, 666,
  };

  public PeopleFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    ArrayList<User> friends = new ArrayList<>();
    for (int i = 0; i < names.length; i++) {
      friends.add(i, new User(names[i], surnames[i], points[i]));
    }
    FriendListAdapter adapter = new FriendListAdapter(this.getActivity(), friends);

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
}
