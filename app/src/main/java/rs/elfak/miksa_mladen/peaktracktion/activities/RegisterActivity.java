package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;
import rs.elfak.miksa_mladen.peaktracktion.providers.UserProvider;
import rs.elfak.miksa_mladen.peaktracktion.utils.Validator;

import static android.Manifest.permission.READ_PHONE_STATE;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
  // Request codes
  static final int REQUEST_IMAGE_CAPTURE = 1;
  static final int REQUEST_PHONE_STATE = 2;

  // UI reference
  private ProgressBar mProgressBar;
  private EditText etEmail;
  private EditText etPassword;
  private EditText etDisplayName;
  private EditText etFullName;
  private EditText etPhone;
  private ImageView imageProfilePic;

  // Auth
  private FirebaseAuth mAuth;

  private String mPhotoPath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    // Setup UI refs
    mProgressBar = (ProgressBar) findViewById(R.id.progress_register);
    etEmail = (EditText) findViewById(R.id.et_email);
    etPassword = (EditText) findViewById(R.id.et_password);
    etDisplayName = (EditText) findViewById(R.id.user_info_display_name);
    etFullName = (EditText) findViewById(R.id.user_info_full_name);
    etPhone = (EditText) findViewById(R.id.et_phone);
    imageProfilePic = (ImageView) findViewById(R.id.image_profile_pic);

    // Setup listeners
    findViewById(R.id.button_picture).setOnClickListener(this);
    findViewById(R.id.button_register_user).setOnClickListener(this);

    // Setup Firebase
    mAuth = FirebaseAuth.getInstance();
  }

  @Override
  public void onStart() {
    super.onStart();
    // Pre-populate phone
    int selfPermission = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);
    boolean noPermission = selfPermission != PackageManager.PERMISSION_GRANTED;

    if (noPermission) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_PHONE_STATE)) {
        // TODO
      } else {
        ActivityCompat.requestPermissions(
          this,
          new String[]{READ_PHONE_STATE},
          REQUEST_PHONE_STATE);
      }
    } else {
      TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
      etPhone.setText(tm.getLine1Number());
    }
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.button_register_user) {
      if (Validator.validateEmail(etEmail) &&
        Validator.validatePassword(etPassword) &&
        Validator.validatePhone(etPhone) &&
        Validator.validateFullName(etFullName) &&
        Validator.validateDisplayName(etDisplayName) &&
        Validator.validatePicture(imageProfilePic)) {
        registerUser();
      }
    } else if (id == R.id.button_picture) {
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
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Glide.with(this)
        .load(mPhotoPath)
        .apply(RequestOptions.circleCropTransform())
        .into(imageProfilePic);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    if (requestCode == REQUEST_PHONE_STATE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
          TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
          etPhone.setText(tm.getLine1Number());
        }
      }
    }
  }

  private void registerUser() {
    mProgressBar.setVisibility(View.VISIBLE);
    final String email = etEmail.getText().toString();
    String password = etPassword.getText().toString();

    mAuth.createUserWithEmailAndPassword(email, password)
      .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (task.isSuccessful()) {
            final String fullName = etFullName.getText().toString();
            final String displayName = etDisplayName.getText().toString();
            final String phone = etPhone.getText().toString();
            UserProvider.getInstance().addNewUser(mAuth.getCurrentUser().getUid(), fullName, email, phone, displayName, mPhotoPath)
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.e("IMAGE", e.getMessage());
                }
              })
              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Uri imageUrl = taskSnapshot.getDownloadUrl();
                  User newUser = new User(mAuth.getCurrentUser().getUid(), fullName, displayName, email, phone, imageUrl.toString());
                  UserProvider.getInstance().setUser(newUser);
                  FirebaseDatabase.getInstance().getReference().child("users").child(newUser.userId).setValue(newUser);
                  finish();
                }
              });
          } else {
            try {
              throw task.getException();
            } catch (FirebaseAuthUserCollisionException ex) {
              etEmail.setError("Postoji korisnik sa ovim emailom!");
            } catch (Exception ex) {
              Toast.makeText(RegisterActivity.this, "Exception: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }
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
