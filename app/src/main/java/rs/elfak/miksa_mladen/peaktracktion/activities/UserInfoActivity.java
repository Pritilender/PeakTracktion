package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;
import rs.elfak.miksa_mladen.peaktracktion.providers.UserProvider;

import static rs.elfak.miksa_mladen.peaktracktion.activities.RegisterActivity.REQUEST_IMAGE_CAPTURE;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
  // Helper constants
  public static final String DISPLAY_ME = "IS_ME_DISPLAYED";
  public static final String USER_ID = "USER_ID";

  boolean isMe;
  boolean editDisplayName = false;
  boolean editFullName = false;
  private String mPhotoPath;
  private User mUser;

  // UI refs
  ImageView ivUserPic;
  EditText etDisplayName;
  EditText etFullName;
  Button btnSave;
  Button btnDiscard;

  private void getUserAndSetupUI(String userId) {
    if (mUser == null) {
      UserProvider.getInstance().getUser(userId)
        .addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = dataSnapshot.getValue(User.class);
            setupUI();
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            Log.e("CANCELLED", "User info activity user cancelled");
          }
        });
    } else {
      setupUI();
    }
  }

  private void setupUI() {
      Glide.with(UserInfoActivity.this)
        .load(mUser.imgUrl)
        .apply(RequestOptions.circleCropTransform())
        .into(ivUserPic);
      etDisplayName.setText(mUser.displayName);
      etFullName.setText(mUser.fullName);
      ((TextView) findViewById(R.id.user_info_phone)).setText(mUser.phone);
      ((TextView) findViewById(R.id.user_info_email)).setText(mUser.email);
      ((TextView) findViewById(R.id.user_info_points)).setText("Broj poena: " + mUser.points);
      ((TextView) findViewById(R.id.user_info_added_places)).setText("Dodatih mesta: " + mUser.createdPlaces.size());
      ((TextView) findViewById(R.id.user_info_visited_places)).setText("Posećenih mesta: " + mUser.createdPlaces.size());
      getSupportActionBar().setTitle(mUser.fullName);
  }

  private void setEditTextEditable(EditText e, boolean val) {
    e.setFocusable(val);
    e.setFocusableInTouchMode(val);
    e.setClickable(val);
    if (val && e.requestFocus()) {
      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
      e.addTextChangedListener(this);
    } else {
      e.removeTextChangedListener(this);
    }
  }

  private void setResultButtonsActive() {
    btnDiscard.setEnabled(true);
    btnSave.setEnabled(true);
  }

  private File createImageFile() throws IOException {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(imageFileName, ".jpg", storageDir);
    mPhotoPath = image.getAbsolutePath();
    return image;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_info);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    isMe = getIntent().getBooleanExtra(DISPLAY_ME, false);

    ivUserPic = (ImageView) findViewById(R.id.user_info_profile_pic);
    etDisplayName = (EditText) findViewById(R.id.user_info_display_name);
    etFullName = (EditText) findViewById(R.id.user_info_full_name);
    btnSave = (Button) findViewById(R.id.user_info_save);
    btnDiscard = (Button) findViewById(R.id.user_info_discard);

    setEditTextEditable(etDisplayName, false);
    setEditTextEditable(etFullName, false);

    if (!isMe) {
      findViewById(R.id.user_info_change_display_name).setVisibility(View.GONE);
      findViewById(R.id.user_info_change_full_name).setVisibility(View.GONE);
      btnSave.setVisibility(View.GONE);
      btnDiscard.setVisibility(View.GONE);
    } else {
      findViewById(R.id.user_info_change_full_name).setOnClickListener(this);
      findViewById(R.id.user_info_change_display_name).setOnClickListener(this);
      findViewById(R.id.user_info_profile_pic).setOnClickListener(this);
      btnSave.setOnClickListener(this);
      btnDiscard.setOnClickListener(this);
    }

  }

  @Override
  public void onStart() {
    super.onStart();
    String userId = getIntent().getStringExtra(USER_ID);
    getUserAndSetupUI(userId);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();

    switch (id) {
      case R.id.user_info_change_display_name:
        editDisplayName = !editDisplayName;
        setEditTextEditable(etDisplayName, editDisplayName);
        break;
      case R.id.user_info_change_full_name:
        editFullName = !editFullName;
        setEditTextEditable(etFullName, editFullName);
        break;
      case R.id.user_info_profile_pic:
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
      case R.id.user_info_save:
        mUser.displayName = etDisplayName.getText().toString();
        mUser.fullName = etFullName.getText().toString();
        if (mPhotoPath != null) {
          UserProvider.getInstance().saveUserImage(Uri.fromFile(new File(mPhotoPath)))
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mUser.imgUrl = taskSnapshot.getDownloadUrl().toString();
                UserProvider.getInstance().updateUser(mUser);
                finish();
              }
            });
        } else {
          UserProvider.getInstance().updateUser(mUser);
          finish();
        }
      case R.id.user_info_discard:
        finish();
    }
  }

  @Override
  public boolean onSupportNavigateUp(){
    finish();
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      mUser.imgUrl = mPhotoPath;
      setResultButtonsActive();
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    // unused
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    // unused
  }

  @Override
  public void afterTextChanged(Editable s) {
    setResultButtonsActive();
  }
}
