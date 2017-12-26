package daluobo.insplash.adapter.vh;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import daluobo.insplash.R;
import daluobo.insplash.helper.NavHelper;
import daluobo.insplash.model.net.Photo;
import daluobo.insplash.util.DimensionUtil;
import daluobo.insplash.util.ImgUtil;
import daluobo.insplash.util.ViewUtil;

/**
 * Created by daluobo on 2017/12/25.
 */

public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    @BindView(R.id.photo_view)
    ImageView mPhotoView;
    @BindView(R.id.description)
    TextView mDescription;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.username)
    TextView mUsername;
    @BindView(R.id.like_btn)
    ImageView mLikeBtn;
    @BindView(R.id.likes)
    TextSwitcher mLikes;
    @BindView(R.id.more)
    ImageView mMore;
    @BindView(R.id.container)
    CardView mContainer;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindDrawable(R.drawable.ic_favorite_border)
    Drawable mIcFavoriteBorder;
    @BindDrawable(R.drawable.ic_favorite)
    Drawable mIcFavorite;
    @BindColor(R.color.colorTitle)
    int mColorTitle;

    TextView mLikeText;

    Context mContext;
    Photo mPhoto;
    int mPosition;
    int mContainerWidth;
    boolean mIsShowUser = true;

    private OnMenuItemClickListener mOnMenuItemClickListener;

    private PopupWindow mPopupWindow;
    private int[] mPopupWindowSize;
    private TextView mDownloadBtn;
    private TextView mCollectBtn;

    public PhotoViewHolder(View itemView, Context context, boolean isShowUser, OnMenuItemClickListener onMenuItemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mContext = context;
        mIsShowUser = isShowUser;
        mOnMenuItemClickListener = onMenuItemClickListener;

        mContainerWidth = ViewUtil.getScreenSize(mContext)[0] - DimensionUtil.dip2px(mContext, 8);
        mPhotoView.setOnClickListener(this);

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toUser(mContext, mPhoto.user, mAvatar);
            }
        });

        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toUser(mContext, mPhoto.user, mAvatar);
            }
        });

        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog(v, mPosition);
            }
        });

        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeBtn.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                onLikeClick(mPhoto);
            }
        });

        mLikes.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                mLikeText = new TextView(mContext);
                mLikeText.setLayoutParams(new TextSwitcher.LayoutParams(TextSwitcher.LayoutParams.MATCH_PARENT, TextSwitcher.LayoutParams.MATCH_PARENT));
                mLikeText.setGravity(Gravity.CENTER);
                mLikeText.setBackgroundColor(Color.TRANSPARENT);
                mLikeText.setTextColor(mColorTitle);
                return mLikeText;
            }
        });

    }

    @Override
    public void onClick(View v) {
        NavHelper.toPhoto(mContext, mPhoto, mPhotoView);
    }

    public void bindDataToView(Photo photo, int position) {
        mPosition = position;

        if (photo == null) {
            return;
        }
        mPhoto = photo;
        ColorDrawable bg = new ColorDrawable(Color.parseColor(photo.color));

        ViewGroup.LayoutParams lp = mPhotoView.getLayoutParams();
        lp.width = mContainerWidth;
        lp.height = lp.width * photo.height / photo.width;
        mPhotoView.setLayoutParams(lp);
        ImgUtil.loadImg(mContext, mPhotoView, bg, photo.urls.small);

        if (photo.description != null) {
            mDescription.setText(photo.description);
            mDescription.setVisibility(View.VISIBLE);
        } else {
            mDescription.setVisibility(View.GONE);
        }

        if (mIsShowUser) {
            mUsername.setText(photo.user.name);
            ImgUtil.loadImg(mContext, mAvatar, photo.user.profile_image.small);
            mUsername.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.VISIBLE);
        } else {
            mUsername.setVisibility(View.GONE);
            mAvatar.setVisibility(View.GONE);
        }

        mLikeBtn.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        if (photo.liked_by_user) {
            mLikeBtn.setImageDrawable(mIcFavorite);
        } else {
            mLikeBtn.setImageDrawable(mIcFavoriteBorder);
        }
        mLikes.setCurrentText(photo.likes + "");
    }

    private void showMoreDialog(View view, final int position) {
        if (mPopupWindow == null) {
            initPopupWindow();
        }

        int[] location = new int[2];
        view.getLocationInWindow(location);

        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - mPopupWindowSize[0], location[1] - mPopupWindowSize[1]);
    }

    private void initPopupWindow() {
        final View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_menu_photo, null, false);
        mPopupWindowSize = ViewUtil.getViewSize(contentView);

        mDownloadBtn = contentView.findViewById(R.id.download_tv);
        mCollectBtn = contentView.findViewById(R.id.collect_tv);

        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownloadClick(mPhoto);
            }
        });

        mCollectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCollectClick(mPhoto);
            }
        });

        mPopupWindow = new PopupWindow(contentView,
                mPopupWindowSize[0],
                mPopupWindowSize[1],
                true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setAnimationStyle(R.style.ScaleAnimation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPopupWindow.setElevation(10);
        }
    }

    public void onLikeClick(Photo photo) {
        mOnMenuItemClickListener.onLikeClick(photo);
    }

    public void onDownloadClick(Photo photo) {
        mOnMenuItemClickListener.onDownloadClick(photo);
    }

    public void onCollectClick(Photo photo) {
        mPopupWindow.dismiss();
        mOnMenuItemClickListener.onCollectClick(photo);
    }

    public interface OnMenuItemClickListener {
        void onLikeClick(Photo photo);

        void onDownloadClick(Photo photo);

        void onCollectClick(Photo photo);
    }
}