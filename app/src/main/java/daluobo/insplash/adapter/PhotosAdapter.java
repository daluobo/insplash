package daluobo.insplash.adapter;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import daluobo.insplash.R;
import daluobo.insplash.adapter.listener.OnActionClickListener;
import daluobo.insplash.adapter.vh.PhotoCardViewHolder;
import daluobo.insplash.adapter.vh.PhotoCompatViewHolder;
import daluobo.insplash.adapter.vh.PhotoViewHolder;
import daluobo.insplash.base.arch.Resource;
import daluobo.insplash.base.arch.ResourceObserver;
import daluobo.insplash.base.view.LoadableAdapter;
import daluobo.insplash.db.model.DownloadInfo;
import daluobo.insplash.download.DownloadService;
import daluobo.insplash.helper.ConfigHelper;
import daluobo.insplash.helper.NavHelper;
import daluobo.insplash.model.net.LikePhoto;
import daluobo.insplash.model.net.Photo;
import daluobo.insplash.model.net.PhotoDownloadLink;
import daluobo.insplash.viewmodel.PhotoViewModel;

/**
 * Created by daluobo on 2017/11/12.
 */

public class PhotosAdapter extends LoadableAdapter<Photo> {
    protected LayoutInflater mInflater;
    protected boolean mIsShowUser = true;
    protected int mColumn = 1;

    protected PhotoViewModel mViewModel;
    protected LifecycleOwner mLifecycleOwner;
    protected FragmentManager mFragmentManager;

    @IntDef({PhotoViewType.COMPAT, PhotoViewType.PREVIEW})
    private @interface PhotoViewType {
        int COMPAT = 10;
        int PREVIEW = 11;
    }

    public PhotosAdapter(Context context, List<Photo> data, LifecycleOwner owner, PhotoViewModel viewModel, FragmentManager manager) {
        super.mContext = context;
        super.mData = data;
        this.mLifecycleOwner = owner;
        this.mViewModel = viewModel;
        this.mFragmentManager = manager;

        mInflater = LayoutInflater.from(mContext);
    }

    public PhotosAdapter(Context context, List<Photo> data, LifecycleOwner owner, PhotoViewModel viewModel, FragmentManager manager, boolean isShowUser, int column) {
        this(context, data, owner, viewModel, manager);
        this.mIsShowUser = isShowUser;
        this.mColumn = column;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowFooter && position == getItemCount() - 1) {
            return ItemViewType.FOOTER_TYPE;
        } else if (ConfigHelper.isCompatView()) {
            return PhotoViewType.COMPAT;
        } else {
            return PhotoViewType.PREVIEW;
        }
    }

    @Override
    protected void bindDataToItemView(RecyclerView.ViewHolder viewHolder, Photo item, int position) {
        if (viewHolder instanceof PhotoCardViewHolder) {
            PhotoCardViewHolder pvh = (PhotoCardViewHolder) viewHolder;
            pvh.bindDataToView(item, position);
        } else if (viewHolder instanceof PhotoCompatViewHolder) {
            PhotoCompatViewHolder cpv = (PhotoCompatViewHolder) viewHolder;
            cpv.bindDataToView(item, position);
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PhotoViewType.COMPAT:
                return new PhotoCompatViewHolder(inflateItemView(parent, R.layout.item_photo_compat), mContext, mColumn, new OnActionClickListener() {
                    @Override
                    public void onLikeClick(final Photo photo) {
                        onPhotoLike(photo);
                    }

                    @Override
                    public void onDownloadClick(Photo photo) {

                    }

                    @Override
                    public void onCollectClick(Photo photo) {

                    }

                });
            case PhotoViewType.PREVIEW:
                return new PhotoCardViewHolder(inflateItemView(parent, R.layout.item_photo_card), mContext, mIsShowUser, new OnActionClickListener() {
                    @Override
                    public void onLikeClick(final Photo photo) {
                        onPhotoLike(photo);
                    }

                    @Override
                    public void onDownloadClick(Photo photo) {
                        onPhotoDownload(photo);
                    }

                    @Override
                    public void onCollectClick(Photo photo) {
                        NavHelper.collectPhoto(mFragmentManager, photo);
                    }
                });
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            PhotoViewHolder pvh = (PhotoViewHolder) holder;

            pvh.mProgressBar.setVisibility(View.GONE);
            pvh.mLikeBtn.setVisibility(View.VISIBLE);
            pvh.mLikes.setText(mData.get(position).likes + "");
            if (mData.get(position).liked_by_user) {
                pvh.mLikeBtn.setImageDrawable(pvh.mIcFavorite);
            } else {
                pvh.mLikeBtn.setImageDrawable(pvh.mIcFavoriteBorder);
            }

            pvh.mLikeBtn.animate()
                    .rotationBy(360)
                    .setDuration(300)
                    .start();
        }
    }

    public void onPhotoLike(final Photo photo) {
        mViewModel.likePhoto(photo).observe(mLifecycleOwner, new ResourceObserver<Resource<LikePhoto>, LikePhoto>(mContext) {
            @Override
            protected void onSuccess(LikePhoto likePhoto) {
                onPhotoLikeChange(likePhoto.photo);
            }

            @Override
            protected void onError(String msg) {
                super.onError(msg);
                onPhotoLikeChange(photo);
            }
        });
    }

    public void onPhotoDownload(final Photo photo) {
        mViewModel.getDownloadLink(photo.id).observe(mLifecycleOwner, new ResourceObserver<Resource<PhotoDownloadLink>, PhotoDownloadLink>(mContext) {
            @Override
            protected void onSuccess(PhotoDownloadLink link) {
                NavHelper.downloadPhoto(mContext, new DownloadInfo(photo, link.url), DownloadService.ACTION_START);
            }
        });
    }

    public void onPhotoLikeChange(Photo photo) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).id.equals(photo.id)) {
                mData.get(i).liked_by_user = photo.liked_by_user;
                mData.get(i).likes = photo.likes;
                notifyItemChanged(i, "photo_change");
                break;
            }
        }
    }

}
