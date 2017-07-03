package rs.elfak.miksa_mladen.peaktracktion.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rs.elfak.miksa_mladen.peaktracktion.utils.Coordinates;

public class BackgroundLocationService extends Service {
  private FirebaseAuth mAuth = FirebaseAuth.getInstance();
  private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

  private class BackgroundLocationListener implements LocationListener {
    private Location mLastLocation;

    public void setLastLocation(Location newLoc) {
      mLastLocation = newLoc;
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
