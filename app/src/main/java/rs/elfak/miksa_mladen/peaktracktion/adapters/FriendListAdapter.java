package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class FriendListAdapter extends ArrayAdapter<User> {

  public FriendListAdapter(Context context, ArrayList<User> friends) {
    super(context, 0, friends);
  }

  @NonNull
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    User friend = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_friends, parent, false);
    }

    TextView tvName = (TextView) convertView.findViewById(R.id.text_name_friend);
    TextView tvPoints = (TextView) convertView.findViewById(R.id.text_points_friend);
    ImageView imageViewUserImage = (ImageView) convertView.findViewById(R.id.image_friend);

    tvName.setText(friend.fullName);
    tvPoints.setText("" + friend.points);
    Glide.with(getContext())
      .load(friend.imgUrl)
      .apply(RequestOptions.circleCropTransform())
      .into(imageViewUserImage);

    return convertView;
  }


}
