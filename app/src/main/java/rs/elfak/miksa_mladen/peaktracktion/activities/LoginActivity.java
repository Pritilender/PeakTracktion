package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.providers.UserProvider;
import rs.elfak.miksa_mladen.peaktracktion.utils.Validator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

  // Firebase
  private FirebaseAuth mAuth;
  private FirebaseAuth.AuthStateListener mAuthStateListener;

  // UI references
  private ProgressBar mProgressBar;
  private EditText etEmail;
  private EditText etPassword;

  private void signIn(String email, String password) {
    mProgressBar.setVisibility(View.VISIBLE);

    mAuth.signInWithEmailAndPassword(email, password)
      .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
          if (!task.isSuccessful()) {
            try {
              throw task.getException();
            } catch (FirebaseAuthInvalidUserException ex) {
              etEmail.setError("Korisnik ne postoji!");
            } catch (FirebaseAuthInvalidCredentialsException ex) {
              etPassword.setError("Pogrešna lozinka!");
            } catch (Exception ex) {
              Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
          } else {
            UserProvider.getInstance()
              .populateUser(mAuth.getCurrentUser().getUid());
          }
          mProgressBar.setVisibility(View.INVISIBLE);
        }
      });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Setup UI
    mProgressBar = (ProgressBar) findViewById(R.id.progress_auth);
    etEmail = (EditText) findViewById(R.id.et_email);
    etPassword = (EditText) findViewById(R.id.et_password);

    // Setup click listeners
    findViewById(R.id.button_sign_in).setOnClickListener(this);
    findViewById(R.id.button_forgot_password).setOnClickListener(this);
    findViewById(R.id.button_register).setOnClickListener(this);

    // Setup Firebase
    mAuth = FirebaseAuth.getInstance();
    mAuthStateListener = new FirebaseAuth.AuthStateListener() {
      @Override
      public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
          // user is signed in
          UserProvider.getInstance().populateUser(user.getUid());
          finish();
        }
      }
    };
  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthStateListener);
  }

  @Override
  public void onStop() {
    super.onStop();
    mAuth.removeAuthStateListener(mAuthStateListener);
  }

  @Override
  public void onBackPressed() {
    moveTaskToBack(true);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_sign_in:
        if (Validator.validateEmail(etEmail) && Validator.validatePassword(etPassword)) {
          signIn(etEmail.getText().toString(), etPassword.getText().toString());
        }
        break;
      case R.id.button_forgot_password:
        break;
      case R.id.button_register:
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        break;
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Toast.makeText(this, "Google play services error", Toast.LENGTH_SHORT).show();
  }
}

