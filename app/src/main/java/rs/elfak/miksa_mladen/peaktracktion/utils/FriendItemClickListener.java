package rs.elfak.miksa_mladen.peaktracktion.utils;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import rs.elfak.miksa_mladen.peaktracktion.activities.UserInfoActivity;
import rs.elfak.miksa_mladen.peaktracktion.list_items.User;

public class FriendItemClickListener implements View.OnClickListener {
  private ArrayList<User> mUserArrayList;
  private RecyclerView mRecyclerView;

  public FriendItemClickListener(ArrayList<User> ual, RecyclerView rv) {
    mUserArrayList = ual;
    mRecyclerView = rv;
  }

  @Override
  public void onClick(View v) {
    int itemPosition = mRecyclerView.getChildLayoutPosition(v);
    Intent i = new Intent(mRecyclerView.getContext(), UserInfoActivity.class);
    i.putExtra(UserInfoActivity.USER_ID, mUserArrayList.get(itemPosition).userId);
    i.putExtra(UserInfoActivity.DISPLAY_ME, false);
    mRecyclerView.getContext().startActivity(i);
  }
}
