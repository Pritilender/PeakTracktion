package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.models.Place;

public class EditPlaceActivity extends AppCompatActivity implements View.OnClickListener {
  static final int REQUEST_IMAGE_CAPTURE = 1;
  private EditText name;
  private EditText description;
  private Spinner fidget;
  private String mPhotoPath;
  private ImageView imagePlace;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_place);
    name = (EditText) findViewById(R.id.edit_place_name);
    description = (EditText) findViewById(R.id.edit_place_description);
    fidget = (Spinner) findViewById(R.id.edit_place_type_spinner);
    imagePlace = (ImageView) findViewById(R.id.view_place_image);

    findViewById(R.id.edit_place_OK).setOnClickListener(this);
    findViewById(R.id.edit_place_cancel).setOnClickListener(this);
    findViewById(R.id.edit_place_add_picture).setOnClickListener(this);

    String temp = getIntent().getStringExtra("titleBar");
    ActionBar header = getSupportActionBar();
    header.setTitle(temp);
    header.setDisplayHomeAsUpEnabled(true);
    if (!temp.equals(getString(R.string.title_activity_new_place))) {

    }
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
      case R.id.edit_place_OK:
        savePlace();
        finish();
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

  public boolean savePlace() {
    Place place = new Place();
    place.name = name.getText().toString();
    place.description = description.getText().toString();
    place.type = fidget.getSelectedItem().toString();
    return true;
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
