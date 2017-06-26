package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

import rs.elfak.miksa_mladen.peaktracktion.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
  // Request codes
  public static final int RC_SIGN_IN = 9001;

  // Firebase instances
  private FirebaseAuth mAuth;

  // UI references
  private ProgressBar mProgressBar;

  private GoogleApiClient mGoogleApiClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Setup UI
    mProgressBar = (ProgressBar) findViewById(R.id.progress_auth);

    // Setup click listeners
    findViewById(R.id.button_login_google).setOnClickListener(this);

    // setup Google Sign In
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestIdToken(getString(R.string.default_web_client_id))
      .requestEmail()
      .build();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
      .enableAutoManage(this, this)
      .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
      .build();

    // Setup Firebase
    mAuth = FirebaseAuth.getInstance();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_login_google:
        signInGoogleInit();
        break;
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        GoogleSignInAccount account = result.getSignInAccount();
        signInGoogleWithAccount(account);
      } else {
        Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void signInGoogleWithAccount(GoogleSignInAccount account) {
    mProgressBar.setVisibility(View.VISIBLE);

    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (!task.isSuccessful()) {
            Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
          } else {
            goToMainActivity();
          }
          mProgressBar.setVisibility(View.INVISIBLE);
        }
      });
  }

  private void signInGoogleInit() {
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  private void goToMainActivity() {
    Intent mainIntent = new Intent(this, MainActivity.class);
    startActivity(mainIntent);
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Toast.makeText(this, "Google play services error", Toast.LENGTH_SHORT).show();
  }
}

