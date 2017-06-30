package rs.elfak.miksa_mladen.peaktracktion.utils;

import android.content.res.Resources;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.validator.routines.EmailValidator;

public class Validator {
  public static boolean validateEmail(EditText emailField) {
    String email = emailField.getText().toString();

    if (email.isEmpty()) {
      emailField.setError("Obavezno");
      return false;
    }
    if (!EmailValidator.getInstance().isValid(email)) {
      emailField.setError("Loš format email adrese");
      return false;
    }

    return true;
  }

  public static boolean validatePassword(EditText passwordField) {
    String password = passwordField.getText().toString();

    if (password.isEmpty()) {
      passwordField.setError("Obavezno");
      return false;
    }
    if (password.length() < 6) {
      passwordField.setError("Lozinka mora da ima najmanje 6 karaktera");
      return false;
    }
    return true;
  }

  public static boolean validatePhone(EditText phoneField) {
    String phone = phoneField.getText().toString();

    if (phone.isEmpty()) {
      phoneField.setError("Obavezno");
      return false;
    }
    if (phone.length() < 9) {
      phoneField.setError("Neispravan format broja telefona!");
      return false;
    }

    return true;
  }

  public static boolean validateFullName(EditText etFullName) {
    if (etFullName.getText().toString().isEmpty()) {
      etFullName.setError("Morate uneti ime!");
      return false;
    }
    return true;
  }

  public static boolean validateDisplayName(EditText etDisplayName) {
    if (etDisplayName.getText().toString().isEmpty()) {
      etDisplayName.setError("Morate uneti ime za prikaz!");
      return false;
    }
    return true;
  }

  public static boolean validatePicture(ImageView imageProfilePic) {
    if (imageProfilePic.getDrawable() == null) {
      Toast.makeText(imageProfilePic.getContext(), "Morate uneti sliku!", Toast.LENGTH_SHORT).show();
      return false;
    }
    return true;
  }
}
