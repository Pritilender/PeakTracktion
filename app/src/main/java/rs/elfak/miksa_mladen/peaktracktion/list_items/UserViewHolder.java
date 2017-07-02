package rs.elfak.miksa_mladen.peaktracktion.list_items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import rs.elfak.miksa_mladen.peaktracktion.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
  public TextView tvFullName;
  public TextView tvDisplayName;
  public TextView tvPoints;
  public ImageView ivUserImage;

  public UserViewHolder(View itemView) {
    super(itemView);
    tvFullName = (TextView) itemView.findViewById(R.id.friend_full_name);
    tvDisplayName = (TextView) itemView.findViewById(R.id.friend_display_name);
    tvPoints = (TextView) itemView.findViewById(R.id.friend_points);
    ivUserImage = (ImageView) itemView.findViewById(R.id.friend_profile_pic);
  }
}
