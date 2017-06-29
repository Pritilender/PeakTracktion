package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.fragments.AboutFragment;
import rs.elfak.miksa_mladen.peaktracktion.fragments.MapFragment;
import rs.elfak.miksa_mladen.peaktracktion.fragments.PeopleFragment;
import rs.elfak.miksa_mladen.peaktracktion.fragments.PlacesFragment;
import rs.elfak.miksa_mladen.peaktracktion.fragments.ScoreboardFragment;
import rs.elfak.miksa_mladen.peaktracktion.fragments.SettingsFragment;
import rs.elfak.miksa_mladen.peaktracktion.services.BackgroundLocationService;

public class MainActivity extends AppCompatActivity
  implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

  // Auth
  private FirebaseAuth mAuth;

  // UI references
  private ImageView mUserPicture;
  private Button mUserName;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

    // UI setup
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    // set default selected item in navigation view
    onNavigationItemSelected(navigationView.getMenu().getItem(0));
    navigationView.setCheckedItem(R.id.nav_map);

    // Header setup
    View headerView = navigationView.getHeaderView(0);
    mUserName = (Button) headerView.findViewById(R.id.button_user_name);
    mUserPicture = (ImageView) headerView.findViewById(R.id.image_user);
    mUserName.setText(user.getDisplayName());
    mUserName.setOnClickListener(this);
    Glide.with(headerView)
      .load(user.getPhotoUrl())
      .apply(RequestOptions.circleCropTransform())
      .into(mUserPicture);
    headerView.findViewById(R.id.button_sign_out).setOnClickListener(this);
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.nav_map:
        replaceFragment(new MapFragment(), "Map");
        break;
      case R.id.nav_places:
        replaceFragment(new PlacesFragment(), "Places");
        break;
      case R.id.nav_friends:
        replaceFragment(new PeopleFragment(), "Friends");
        break;
      case R.id.nav_scoreboard:
        replaceFragment(new ScoreboardFragment(), "Scoreboard");
        break;
      case R.id.nav_settings:
        replaceFragment(new SettingsFragment(), "Settings");
        break;
      case R.id.nav_about:
        replaceFragment(new AboutFragment(), "About");
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void replaceFragment(Fragment newFragment, String newTitle) {
    getSupportFragmentManager().beginTransaction()
      .replace(R.id.fragment_main, newFragment)
      .commit();
    setTitle(newTitle);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();

    switch(id) {
      case R.id.button_sign_out:
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        finish();
        break;
      case R.id.button_user_name:
        Toast.makeText(this, "Should enter user info activity", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}
