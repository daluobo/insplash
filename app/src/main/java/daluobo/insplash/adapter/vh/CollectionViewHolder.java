package daluobo.insplash.adapter.vh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import daluobo.insplash.R;
import daluobo.insplash.helper.ConfigHelper;
import daluobo.insplash.helper.NavHelper;
import daluobo.insplash.model.net.Collection;
import daluobo.insplash.util.DimensionUtil;
import daluobo.insplash.util.ImgUtil;
import daluobo.insplash.util.ViewUtil;

/**
 * Created by daluobo on 2018/1/9.
 */

public abstract class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.preview_0)
    ImageView mPreview0;
    @BindView(R.id.preview_1)
    ImageView mPreview1;
    @BindView(R.id.preview_2)
    ImageView mPreview2;
    @BindView(R.id.photos_container)
    LinearLayout mPhotosContainer;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.description)
    TextView mDescription;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.total_photos)
    TextView mTotalPhotos;

    @BindString(R.string.photos)
    String mPhotosStr;
    @BindDrawable(R.drawable.ic_neutral)
    Drawable mIcNeutral;

    Context mContext;
    Collection mCollection;
    int mPosition;
    int mContainerWidth = 0;
    boolean mIsShowUser = true;


    public CollectionViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mContext = context;

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHelper.toUser(mContext, mCollection.user, mAvatar);
            }
        });

        mContainerWidth = ViewUtil.getScreenSize(mContext)[0];
        if(!ConfigHelper.isCompatView()){
            mContainerWidth = mContainerWidth - DimensionUtil.dpToPx(16);
        }
    }

    public void bindDataToView(Collection collection, int position) {
        mCollection = collection;
        mPosition = position;

        mPreview0.setVisibility(View.VISIBLE);
        mPreview1.setVisibility(View.VISIBLE);
        mPreview2.setVisibility(View.VISIBLE);

        mTitle.setText(collection.title);
        mTotalPhotos.setText(collection.total_photos + "  " + mPhotosStr);
        if (mIsShowUser) {
            mAvatar.setVisibility(View.VISIBLE);
            ImgUtil.loadImg(mContext, mAvatar, collection.user.profile_image.small);
        } else {
            mAvatar.setVisibility(View.GONE);
        }

        if (collection.description != null) {
            mDescription.setText(collection.description);
            mDescription.setVisibility(View.VISIBLE);
        } else {
            mDescription.setVisibility(View.GONE);
        }

        if (collection.cover_photo == null) {
            mPreview0.setVisibility(View.INVISIBLE);
            mPreview2.setVisibility(View.INVISIBLE);

            ViewGroup.LayoutParams lp = mPhotosContainer.getLayoutParams();
            lp.width = mContainerWidth;
            lp.height = lp.width / 3;

            mPhotosContainer.setLayoutParams(lp);
            mPreview1.setImageDrawable(mIcNeutral);

            return;
        }

        ColorDrawable bg = new ColorDrawable(Color.parseColor(collection.cover_photo.color));
        ViewGroup.LayoutParams containerLp = mPhotosContainer.getLayoutParams();

        if (collection.preview_photos.size() >= 3) {
            containerLp.height = mContainerWidth / 3;
            mPhotosContainer.setLayoutParams(containerLp);

            ImgUtil.loadImgCC(mContext, mPreview0, bg, collection.preview_photos.get(0).urls.thumb);
            ImgUtil.loadImgCC(mContext, mPreview1, bg, collection.preview_photos.get(1).urls.thumb);
            ImgUtil.loadImgCC(mContext, mPreview2, bg, collection.preview_photos.get(2).urls.thumb);
        } else if (collection.preview_photos.size() == 2) {
            mPreview1.setVisibility(View.VISIBLE);
            mPreview2.setVisibility(View.GONE);

            containerLp.height = mContainerWidth / 2;
            mPhotosContainer.setLayoutParams(containerLp);

            ImgUtil.loadImgCC(mContext, mPreview0, bg, collection.preview_photos.get(0).urls.small);
            ImgUtil.loadImgCC(mContext, mPreview1, bg, collection.preview_photos.get(1).urls.small);

        } else {
            mPreview1.setVisibility(View.GONE);
            mPreview2.setVisibility(View.GONE);

            containerLp.height = mContainerWidth * 2 / 3;
            mPhotosContainer.setLayoutParams(containerLp);

            if (collection.cover_photo != null) {
                ImgUtil.loadImgCC(mContext, mPreview0, bg, collection.cover_photo.urls.regular);
            } else {
                mPreview0.setImageDrawable(mIcNeutral);
            }
        }
    }


    @Override
    public void onClick(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];

        NavHelper.toCollection(mContext, mCollection, x + view.getWidth() / 2, y + mPreview0.getHeight() / 2);
    }
}
