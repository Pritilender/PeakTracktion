package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.R;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

/**
 * Created by miksa on 27.6.17..
 */

public class FriendListAdapter extends ArrayAdapter<User> {

  public FriendListAdapter(Context context, ArrayList<User> friends) {
    super(context, 0, friends);
  }

  public View getView(int position, View convertView, ViewGroup parent) {
    User friend = getItem(position);
    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_friends, parent, false);
    }
    TextView tvName = (TextView) convertView.findViewById(R.id.text_name_friend);
    TextView tvSurname = (TextView) convertView.findViewById(R.id.text_surname_friend);
    TextView tvPoints = (TextView) convertView.findViewById(R.id.text_points_friend);
    ImageView imageViewUserImage = (ImageView) convertView.findViewById(R.id.image_friend);

    tvName.setText(friend.firstName + " ");
    tvSurname.setText(friend.lastName);
    tvPoints.setText("" + friend.obtainedPoints);
    Glide.with(getContext())
      .load(friend.imgURL)
      .apply(RequestOptions.circleCropTransform())
      .into(imageViewUserImage);


    return convertView;
  }


}
