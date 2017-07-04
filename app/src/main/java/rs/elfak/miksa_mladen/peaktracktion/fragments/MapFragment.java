package rs.elfak.miksa_mladen.peaktracktion.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.activities.EditPlaceActivity;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;
import rs.elfak.miksa_mladen.peaktracktion.models.User;
import rs.elfak.miksa_mladen.peaktracktion.services.BackgroundLocationService;
import rs.elfak.miksa_mladen.peaktracktion.utils.UsersGeoQueryListener;

public class MapFragment extends Fragment
  implements OnMapReadyCallback,
  GoogleMap.OnMapClickListener,
  GoogleMap.OnMarkerClickListener,
  GeoQueryEventListener,
  LocationListener,
  SeekBar.OnSeekBarChangeListener {

  // constants
  private final String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
  public static final int REQUEST_LOCATION_PERMISSION = 99;
  private GeoLocation mLocation = new GeoLocation(0, 0);
  private final int MAX_SLIDER = 100001;

  // map things
  private Bundle mBundle;
  private MapView mMapView;
  private GoogleMap mMap;
  private Circle mCircle;
  private BiMap<String, Marker> mPoiMarkers = HashBiMap.create();

  // GeoFire
  private GeoFire mGeoFire;
  private GeoQuery mGeoQuery;
  private GeoFire mUserGeoFire;
  private GeoQuery mUserGeoQuery;
  private double mRadius = 1000;
  private LocationManager mLocationManager;
  private String locationProvider;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBundle = savedInstanceState;

    mGeoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("placesGeoFire"));
    mUserGeoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("usersGeoFire"));
    mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(mLocation.latitude, mLocation.longitude),
      mRadius / 1000);
    mGeoQuery.addGeoQueryEventListener(this);

    mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    Criteria critera = new Criteria();
    locationProvider = mLocationManager.getBestProvider(critera, false);
    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED) {
      return;
    }
    Location loc = mLocationManager.getLastKnownLocation(locationProvider);
    if (loc != null) {
      onLocationChanged(loc);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup cont, Bundle savedInstanceState) {
    View inflatedView = inflater.inflate(R.layout.fragment_map, cont, false);

    FloatingActionButton fab = (FloatingActionButton) inflatedView.findViewById(R.id.map_fragment_fab_newplace);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), EditPlaceActivity.class);
        startActivity(intent);
      }
    });

    SeekBar seekBar = (SeekBar) inflatedView.findViewById(R.id.map_fragment_seekbar);
    seekBar.setOnSeekBarChangeListener(this);
    seekBar.setMax(MAX_SLIDER);
    seekBar.setProgress((int)mRadius);

    try {
      MapsInitializer.initialize(getActivity());
    } catch (Exception e) {
      Log.d("error", "something with " + e.getMessage());
    }

    mMapView = (MapView) inflatedView.findViewById(R.id.map_fragment_map);
    mMapView.onCreate(mBundle);
    if (mMap == null) {
      ((MapView) inflatedView.findViewById(R.id.map_fragment_map)).getMapAsync(this);
    }

    return inflatedView;
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f));
    mMap.setOnMarkerClickListener(this);
    mUserGeoQuery = mUserGeoFire.queryAtLocation(mLocation, mRadius / 1000);
    mUserGeoQuery.addGeoQueryEventListener(new UsersGeoQueryListener(mMap));

    UiSettings mapUi = mMap.getUiSettings();
    mapUi.setCompassEnabled(true);
    checkLocationPermission();
  }

  @Override
  public void onMapClick(LatLng latLng) {
    Log.d("Event", "Map clicked");
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    String key = mPoiMarkers.inverse().get(marker);
    Toast.makeText(getActivity(), (String)marker.getTag(), Toast.LENGTH_SHORT).show();
//    FirebaseDatabase.getInstance().getReference().child("places").child(key)
//      .addListenerForSingleValueEvent(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//          Toast.makeText(getActivity(), "Place clicked " + dataSnapshot.getValue(Place.class).name, Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//        }
//      });
    return true;
  }

  @Override
  public void onResume() {
    mMapView.onResume();
    super.onResume();

    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED) {
      return;
    }
    mLocationManager.requestLocationUpdates(locationProvider, 400, 1.0f, this);
  }

  @Override
  public void onPause() {
    super.onPause();
    mLocationManager.removeUpdates(this);
  }

  @Override
  public void onDestroy() {
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
                REQUEST_LOCATION_PERMISSION);
            }
          })
          .create()
          .show();
      } else {
        requestPermissions(
          new String[]{locationPermission}, REQUEST_LOCATION_PERMISSION);
      }
    } else {
      mMap.setMyLocationEnabled(true);
      LocationServices.getFusedLocationProviderClient(this.getActivity()).getLastLocation()
        .addOnSuccessListener(new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, mMap.getCameraPosition().zoom));
            mCircle = mMap.addCircle(new CircleOptions().center(center).radius(mRadius));
            mCircle.setFillColor(Color.argb(60, 255, 0, 0));
            mCircle.setStrokeColor(Color.argb(100, 255, 255, 255));
          }
        });
      Intent intent = new Intent(getActivity(), BackgroundLocationService.class);
      getActivity().startService(intent);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case REQUEST_LOCATION_PERMISSION: {
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (ContextCompat.checkSelfPermission(this.getActivity(), locationPermission)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationServices.getFusedLocationProviderClient(this.getActivity()).getLastLocation()
              .addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
                }
              });
            Intent intent = new Intent(getActivity(), BackgroundLocationService.class);
            getActivity().startService(intent);
          }
        }
      }
    }
  }

  @Override
  public void onKeyEntered(String key, GeoLocation location) {
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(new LatLng(location.latitude, location.longitude));
    if (mMap != null) {
      Marker marker = mMap.addMarker(markerOptions);
      marker.setTag("Place " + key);
      mPoiMarkers.put(key, marker);
    }
  }

  @Override
  public void onKeyExited(String key) {
    Marker marker = mPoiMarkers.remove(key);
    marker.remove();
  }

  @Override
  public void onKeyMoved(String key, GeoLocation location) {
    mPoiMarkers.get(key).setAnchor((float) location.latitude, (float) location.longitude);
  }

  @Override
  public void onGeoQueryReady() {

  }

  @Override
  public void onGeoQueryError(DatabaseError error) {
    new AlertDialog.Builder(this.getContext())
      .setTitle("Error")
      .setMessage("There was an unexpected error querying GeoFire: " + error.getMessage())
      .setPositiveButton(android.R.string.ok, null)
      .setIcon(android.R.drawable.ic_dialog_alert)
      .show();
  }

  @Override
  public void onLocationChanged(Location location) {
    if (mCircle != null) {
      mCircle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
    }
    mGeoQuery.setCenter(new GeoLocation(location.getLatitude(), location.getLongitude()));
    mLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {

  }

  @Override
  public void onProviderEnabled(String provider) {

  }

  @Override
  public void onProviderDisabled(String provider) {

  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    if (fromUser) {
      Log.d("RADIUS", "" + progress);
      mGeoQuery.setRadius(progress / 1000.0f);
      mCircle.setRadius(progress);
    }
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {

  }
}
