package rs.elfak.miksa_mladen.peaktracktion.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import rs.elfak.miksa_mladen.peaktracktion.R;

public class MapFragment extends Fragment
  implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

  private Bundle mBundle;
  private MapView mMapView;
  private GoogleMap mMap;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBundle = savedInstanceState;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View inflatedView = inflater.inflate(R.layout.fragment_map, container, false);

    try {
      MapsInitializer.initialize(getActivity());
    } catch (Exception e) {
      Log.d("error", "something with " + e.getMessage());
    }

    mMapView = (MapView) inflatedView.findViewById(R.id.map);
    mMapView.onCreate(mBundle);

    if (mMap == null) {
      ((MapView) inflatedView.findViewById(R.id.map)).getMapAsync(this);
    }

    return inflatedView;

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    // TODO add map things
    // TODO check permissions for map.setMyLocationEnabled(true)
    mMap = googleMap;
//    mMap.setMyLocationEnabled(true);
    UiSettings mapUi = mMap.getUiSettings();
    mapUi.setCompassEnabled(true);
    mapUi.setZoomControlsEnabled(true);
    mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
  }

  @Override
  public void onMapClick(LatLng latLng) {
    Log.d("Event", "Map clicked");
  }

  @Override
  public boolean onMarkerClick(Marker marker) {

    return true;
  }

  @Override
  public void onResume() {
    mMapView.onResume();
    super.onResume();
  }

  @Override
  public void onDestroy() {
    // TODO save context here
    mMapView.onDestroy();
    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    mMapView.onLowMemory();
    super.onLowMemory();
  }
}
