package daluobo.insplash.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import daluobo.insplash.R;
import daluobo.insplash.base.view.FooterAdapter;
import daluobo.insplash.helper.ImgHelper;
import daluobo.insplash.helper.NavHelper;
import daluobo.insplash.model.User;

/**
 * Created by daluobo on 2017/12/7.
 */

public class UsersAdapter extends FooterAdapter<User> {

    private Context mContext;

    public UsersAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    protected void bindDataToItemView(RecyclerView.ViewHolder viewHolder, User item, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;

        holder.mUser = item;
        holder.mUsername.setText(item.username);
        holder.mName.setText(item.name);
        if (item.location != null) {
            holder.mLocation.setText(item.location);
            holder.mLocation.setVisibility(View.VISIBLE);
        } else {
            holder.mLocation.setVisibility(View.GONE);
        }
        ImgHelper.loadImg(mContext, holder.mAvatar, item.profile_image.large);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateItemView(parent, R.layout.item_user));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.avatar)
        ImageView mAvatar;
        @BindView(R.id.username)
        TextView mUsername;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.location)
        TextView mLocation;
        @BindView(R.id.container)
        CardView mContainer;

        User mUser;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavHelper.toUser(mContext, mUser, mAvatar);
        }
    }
}
