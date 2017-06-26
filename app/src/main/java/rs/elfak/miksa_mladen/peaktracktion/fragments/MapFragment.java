package rs.elfak.miksa_mladen.peaktracktion.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationSettingsRequest;
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

  private final String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
  private Bundle mBundle;
  private MapView mMapView;
  private GoogleMap mMap;
  public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBundle = savedInstanceState;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup cont, Bundle savedInstanceState) {
    View inflatedView = inflater.inflate(R.layout.fragment_map, cont, false);

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
    UiSettings mapUi = mMap.getUiSettings();
    mapUi.setCompassEnabled(true);
    mapUi.setZoomControlsEnabled(true);
    mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
    checkLocationPermission();
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

  protected void checkLocationPermission() {
    int selfPermission = ContextCompat.checkSelfPermission(this.getActivity(), locationPermission);
    boolean noPermission = selfPermission != PackageManager.PERMISSION_GRANTED;
    if (noPermission) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), locationPermission)) {
        new AlertDialog.Builder(this.getActivity())
          .setTitle(getString(R.string.location_permission_title))
          .setMessage(getString(R.string.location_permission_text))
          .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              requestPermissions(
                new String[]{locationPermission},
                MY_PERMISSIONS_REQUEST_LOCATION);
            }
          })
          .create()
          .show();
      } else {
        requestPermissions(
          new String[]{locationPermission}, MY_PERMISSIONS_REQUEST_LOCATION);
      }
    } else {
      mMap.setMyLocationEnabled(true);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_LOCATION: {
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (ContextCompat.checkSelfPermission(this.getActivity(), locationPermission)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
          }
        }
      }
    }
  }
}
