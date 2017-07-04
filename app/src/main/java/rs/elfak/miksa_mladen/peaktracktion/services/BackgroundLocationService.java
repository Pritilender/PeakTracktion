package rs.elfak.miksa_mladen.peaktracktion.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.activities.ViewPlaceActivity;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;
import rs.elfak.miksa_mladen.peaktracktion.utils.Coordinates;

public class BackgroundLocationService extends Service implements GeoQueryEventListener {
  private FirebaseAuth mAuth = FirebaseAuth.getInstance();
  private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
  private GeoFire mGeoFire = new GeoFire(mDatabase.child("placesGeoFire"));
  private GeoQuery mGeoQuery = mGeoFire.queryAtLocation(new GeoLocation(0, 0), 10);
  private NotificationCompat.Builder mNotifBuilder;

  @Override
  public void onKeyEntered(String key, GeoLocation location) {
    mDatabase.child("places").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        final Place p = dataSnapshot.getValue(Place.class);
        mNotifBuilder = new NotificationCompat.Builder(getApplicationContext())
          .setSmallIcon(R.drawable.ic_flag)
          .setContentTitle("Stigli ste na " + p.name)
          .setContentText("Osvojili ste " + p.points + " poena! Kliknite ovde da biste ga videli u aplikaciji.");

        Intent i = new Intent(getApplicationContext(), ViewPlaceActivity.class);
        i.putExtra("PLACE_ID", p.placeId);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(001, mNotifBuilder.build());

        mDatabase.child("places").child(p.placeId).child("timesVisited").setValue(p.timesVisited++);
        mDatabase.child("places").child(p.placeId).child("points").setValue(p.points + 10);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("placesVisited").child(p.placeId).setValue(true);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("points").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            long points = (long) dataSnapshot.getValue();
            points += p.points;
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("points").setValue(points);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onKeyExited(String key) {

  }

  @Override
  public void onKeyMoved(String key, GeoLocation location) {

  }

  @Override
  public void onGeoQueryReady() {

  }

  @Override
  public void onGeoQueryError(DatabaseError error) {

  }

  private class BackgroundLocationListener implements LocationListener {
    private Location mLastLocation;

    public void setLastLocation(Location newLoc) {
      mLastLocation = newLoc;
      mGeoQuery.setCenter(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
      FirebaseUser user = mAuth.getCurrentUser();
      if (user != null) {
        try {
          Coordinates newLocation = new Coordinates(mLastLocation.getLatitude(), mLastLocation.getLongitude());
          mDatabase.child("users").child(user.getUid()).child("location")
            .setValue(newLocation);
        } catch (Exception e) {
          Log.e("EXC", e.getMessage());
        }
      }
    }

    public BackgroundLocationListener(String provider) {
      setLastLocation(new Location(provider));
    }

    @Override
    public void onLocationChanged(Location location) {
      Log.d("GPS", "Location changed");
      Toast.makeText(BackgroundLocationService.this, "Location changed", Toast.LENGTH_SHORT).show();
      setLastLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
//      Toast.makeText(BackgroundLocationService.this, "Status changed for provider: " + provider + " " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
//      Toast.makeText(BackgroundLocationService.this, "Provider enabled: " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
//      Toast.makeText(BackgroundLocationService.this, "Provider disabled: " + provider, Toast.LENGTH_SHORT).show();
    }
  }

  private LocationManager mLocationManager;
  private static final int LOCATION_INTERVAL = 1000;
  private static final float LOCATION_DISTANCE = 10f;
  LocationListener[] mLocationListeners = new LocationListener[]{
    new BackgroundLocationListener(LocationManager.GPS_PROVIDER),
    new BackgroundLocationListener(LocationManager.NETWORK_PROVIDER)
  };

  public BackgroundLocationService() {
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d("GPS", "On start command");
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    Log.e("GPS", "onCreate");
    initializeLocationManager();
    try {
      mLocationManager.requestLocationUpdates(
        LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
        mLocationListeners[1]);
    } catch (java.lang.SecurityException ex) {
      Log.e("GPS", "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
      Log.e("GPS", "network provider does not exist, " + ex.getMessage());
    }
    try {
      mLocationManager.requestLocationUpdates(
        LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
        mLocationListeners[0]);
    } catch (java.lang.SecurityException ex) {
      Log.e("GPS", "fail to request location update, ignore", ex);
    } catch (IllegalArgumentException ex) {
      Log.e("GPS", "gps provider does not exist " + ex.getMessage());
    }

    mAuth = FirebaseAuth.getInstance();
    mGeoQuery.addGeoQueryEventListener(this);
  }

  @Override
  public void onDestroy() {
    Log.e("GPS", "onDestroy");
    super.onDestroy();
    if (mLocationManager != null) {
      for (int i = 0; i < mLocationListeners.length; i++) {
        try {
          mLocationManager.removeUpdates(mLocationListeners[i]);
        } catch (Exception ex) {
          Log.i("GPS", "fail to remove location listners, ignore", ex);
        }
      }
    }
  }

  private void initializeLocationManager() {
    Log.e("GPS", "initializeLocationManager");
    if (mLocationManager == null) {
      mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }
  }
}
