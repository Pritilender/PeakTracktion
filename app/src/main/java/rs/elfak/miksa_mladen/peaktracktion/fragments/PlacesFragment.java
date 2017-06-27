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
import rs.elfak.miksa_mladen.peaktracktion.adapters.PlacesAdapter;
import rs.elfak.miksa_mladen.peaktracktion.list_items.Place;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

  private ListView list;

  private final String[] itemName = {
    "Trem",
    "Koritnjak",
    "Sokolov Kamen",
    "Jelenin grad",
    "Mon Blam",
    "Pasarelo",
    "Crni Kamen",
    "Mosor",
    "Niska Banja",
    "Midzor",
    "Djeravica",
    "Poskok"
  };

  private final String[] itemDesc = {
    "Najvisi vrh suve planine",
    "Korito koje njace",
    "Najlepsi pogled na Nis imate sa ove tacke",
    "Odnosno kurvin grad",
    "Najvisi vrh Evrope",
    "Davno bejasmo tamo",
    "Tu nas napade poskok",
    "Alpinisticka tacka :D",
    "Topla voda",
    "Najvisi vrh srbije",
    "Najvisi vrh SRBIJE!",
    "zmija"
  };

  public PlacesFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment

    ArrayList<Place> arrayOfPlaces = new ArrayList<>();
    for (int i = 0; i < itemName.length; i++) {
      arrayOfPlaces.add(i, new Place(itemName[i], itemDesc[i]));
    }

    PlacesAdapter adapter = new PlacesAdapter(this.getActivity(), arrayOfPlaces);

    View view = inflater.inflate(R.layout.fragment_places, container, false);
    list = (ListView) view.findViewById(R.id.list_view_places);
    list.setAdapter(adapter);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
                              int position, long id) {
        // TODO Auto-generated method stub
        String selectedItem = itemName[position];
        Toast.makeText(getActivity(), selectedItem, Toast.LENGTH_SHORT).show();

      }
    });

    return view;
  }

}
