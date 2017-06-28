package rs.elfak.miksa_mladen.peaktracktion.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.Place;

public class EditPlaceActivity extends AppCompatActivity implements View.OnClickListener {

  private EditText name;
  private EditText description;
  private Spinner fidget;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_place);
    name = (EditText) findViewById(R.id.edit_view_name_place);
    description = (EditText) findViewById(R.id.edit_view_description_place);
    fidget = (Spinner) findViewById(R.id.spinner_type_place);
    findViewById(R.id.button_ok_editplace).setOnClickListener(this);
    findViewById(R.id.button_cancel_editplace).setOnClickListener(this);;
    findViewById(R.id.button_add_picture_place).setOnClickListener(this);;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_cancel_editplace:
        finish();
        break;
      case R.id.button_ok_editplace:
        savePlace();
        break;
      case R.id.button_add_picture_place:
        Toast.makeText(this, "Opens to select / take picture", Toast.LENGTH_SHORT).show();
        break;
    }
  }

  public boolean savePlace() {
    Place place = new Place();
    place.name = name.getText().toString();
    place.description = description.getText().toString();
    place.type = fidget.getSelectedItem().toString();
    return true;
  }


}
