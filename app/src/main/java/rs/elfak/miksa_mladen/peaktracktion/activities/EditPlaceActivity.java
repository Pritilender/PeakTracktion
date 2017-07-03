package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rs.elfak.miksa_mladen.peaktracktion.Manifest;
import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;
import rs.elfak.miksa_mladen.peaktracktion.services.BackgroundLocationService;
import rs.elfak.miksa_mladen.peaktracktion.utils.Coordinates;

public class EditPlaceActivity extends AppCompatActivity implements View.OnClickListener {
  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_LOCATION = 1;
  private final String locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
  private EditText etName;
  private EditText etDescription;
  private Spinner spTypes;
  private String mPhotoPath;
  private ImageView imagePlace;
  private FusedLocationProviderClient mFusedLocation;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_place);

    etName = (EditText) findViewById(R.id.edit_place_name);
    etDescription = (EditText) findViewById(R.id.edit_place_description);
    spTypes = (Spinner) findViewById(R.id.edit_place_type_spinner);
    imagePlace = (ImageView) findViewById(R.id.view_place_image);

    findViewById(R.id.edit_place_save).setOnClickListener(this);
    findViewById(R.id.edit_place_cancel).setOnClickListener(this);
    findViewById(R.id.edit_place_add_picture).setOnClickListener(this);

    ActionBar header = getSupportActionBar();
    header.setTitle(R.string.title_activity_edit_place);
    header.setDisplayHomeAsUpEnabled(true);

    mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Glide.with(this)
        .load(mPhotoPath)
        .into(imagePlace);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.edit_place_cancel:
        finish();
        break;
      case R.id.edit_place_save:
        int selfPermission = ContextCompat.checkSelfPermission(this, locationPermission);
        boolean noPermission = selfPermission != PackageManager.PERMISSION_GRANTED;
        if (noPermission) {
          if (ActivityCompat.shouldShowRequestPermissionRationale(this, locationPermission)) {
            new AlertDialog.Builder(this)
              .setTitle(getString(R.string.location_permission_title))
              .setMessage(getString(R.string.location_permission_text))
              .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  ActivityCompat.requestPermissions(getParent(),
                    new String[]{locationPermission},
                    REQUEST_LOCATION);
                }
              })
              .create()
              .show();
          } else {
            ActivityCompat.requestPermissions(getParent(), new String[]{locationPermission}, REQUEST_LOCATION);
          }
        } else {
          mFusedLocation.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
              @Override
              public void onSuccess(Location location) {
                String name = etName.getText().toString();
                String description = etDescription.getText().toString();
                String type = spTypes.getSelectedItem().toString();
                Coordinates coords = new Coordinates(location.getLatitude(), location.getLongitude());
                savePlace(name, description, type, coords);
                finish();
              }
            });
        }
        break;
      case R.id.edit_place_add_picture:
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
          File photoFile = null;
          try {
            photoFile = createImageFile();
          } catch (IOException ex) {
            Log.e("IMAGE", ex.getMessage());
          }
          if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, "rs.elfak.miksa_mladen.peaktracktion.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
          }
        }
        break;
      case android.R.id.home:
        this.finish();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case REQUEST_LOCATION: {
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (ContextCompat.checkSelfPermission(this, locationPermission) ==
            PackageManager.PERMISSION_GRANTED) {
            mFusedLocation.getLastLocation()
              .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                  String name = etName.getText().toString();
                  String description = etDescription.getText().toString();
                  String type = spTypes.getSelectedItem().toString();
                  Coordinates coords = new Coordinates(location.getLatitude(), location.getLongitude());
                  savePlace(name, description, type, coords);
                  finish();
                }
              });
          }
        }
      }
    }
  }

  public void savePlace(String name, String description, String type, Coordinates coords) {
    String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Place place = new Place(name, description, mPhotoPath, userKey, coords, type);
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String newPlaceKey = dbRef.child("users").child(userKey).child("createdPlaces").push().getKey();
    place.placeId = newPlaceKey;
    dbRef.child("places").child(newPlaceKey).setValue(place).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override
      public void onSuccess(Void aVoid) {
        finish();
      }
    });
  }

  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(imageFileName, ".jpg", storageDir);
    mPhotoPath = image.getAbsolutePath();
    return image;
  }
}
