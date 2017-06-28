package rs.elfak.miksa_mladen.peaktracktion.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
    TextView textView_info = (TextView) convertView.findViewById(R.id.text_details_friend);
    ImageView imageView_image = (ImageView) convertView.findViewById(R.id.image_friend);

    imageView_image.setImageResource(R.mipmap.ic_launcher);
    //This may or may not work
    textView_info.setText(friend.name);

    return convertView;
  }


}
