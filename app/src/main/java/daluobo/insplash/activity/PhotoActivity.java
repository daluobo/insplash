package daluobo.insplash.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import daluobo.insplash.R;
import daluobo.insplash.base.arch.Resource;
import daluobo.insplash.base.arch.ResourceObserver;
import daluobo.insplash.base.view.BaseActivity;
import daluobo.insplash.db.model.DownloadInfo;
import daluobo.insplash.download.DownloadService;
import daluobo.insplash.event.PhotoChangeEvent;
import daluobo.insplash.helper.AnimHelper;
import daluobo.insplash.helper.AuthHelper;
import daluobo.insplash.helper.NavHelper;
import daluobo.insplash.model.net.LikePhoto;
import daluobo.insplash.model.net.Photo;
import daluobo.insplash.model.net.PhotoDownloadLink;
import daluobo.insplash.util.DateUtil;
import daluobo.insplash.util.ImgUtil;
import daluobo.insplash.util.ToastUtil;
import daluobo.insplash.util.ViewUtil;
import daluobo.insplash.viewmodel.PhotoDetailViewModel;

public class PhotoActivity extends BaseActivity {
    public static final String ARG_PHOTO = "photo";
    protected PhotoDetailViewModel mViewModel;
    protected ColorDrawable mPhotoColor;
    protected int mPhotoColorId;

    @BindDrawable(R.drawable.ic_favorite_border)
    Drawable mIcFavoriteBorder;
    @BindDrawable(R.drawable.ic_favorite)
    Drawable mIcFavorite;
    @BindDrawable(R.drawable.ic_visibility)
    Drawable mIcVisibility;
    @BindDrawable(R.drawable.ic_file_download)
    Drawable mIcDownload;
    @BindDrawable(R.drawable.ic_place)
    Drawable mIcPlace;
    @BindDrawable(R.drawable.ic_time)
    Drawable mIcTime;
    @BindDrawable(R.drawable.ic_camera)
    Drawable mIcCamera;
    @BindDrawable(R.drawable.ic_arrow_down)
    Drawable mIcArrowDown;
    @BindDrawable(R.drawable.ic_straighten)
    Drawable mIcStraighten;
    @BindDrawable(R.drawable.ic_palette)
    Drawable mIcPalette;
    @BindDrawable(R.drawable.ic_person_outline)
    Drawable mIcPersonOutline;
    @BindDrawable(R.drawable.ic_mark_border)
    Drawable mIcMarkBorder;
    @BindDrawable(R.drawable.ic_mark)
    Drawable mIcMark;
    @BindView(R.id.photo_view)
    ImageView mPhotoView;
    @BindView(R.id.description)
    TextView mDescription;
    @BindView(R.id.like_count)
    TextView mLikeCount;
    @BindView(R.id.views_count)
    TextView mViewsCount;
    @BindView(R.id.download_count)
    TextView mDownloadCount;
    @BindView(R.id.location)
    TextView mLocation;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.exif_model)
    TextView mExifModel;
    @BindView(R.id.exif_container_hint)
    ImageView mExifContainerHint;
    @BindView(R.id.show_exif_btn)
    RelativeLayout mShowExifBtn;
    @BindView(R.id.label_exposure_time)
    TextView mLabelExposureTime;
    @BindView(R.id.exposure_time)
    TextView mExposureTime;
    @BindView(R.id.label_aperture)
    TextView mLabelAperture;
    @BindView(R.id.aperture)
    TextView mAperture;
    @BindView(R.id.label_focal_length)
    TextView mLabelFocalLength;
    @BindView(R.id.focal_length)
    TextView mFocalLength;
    @BindView(R.id.label_iso)
    TextView mLabelIso;
    @BindView(R.id.iso)
    TextView mIso;
    @BindView(R.id.exif_container)
    LinearLayout mExifContainer;
    @BindView(R.id.size)
    TextView mSize;
    @BindView(R.id.color)
    TextView mColor;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.username)
    TextView mUsername;
    @BindView(R.id.user_container)
    LinearLayout mUserContainer;
    @BindView(R.id.collect_btn)
    TextView mCollectBtn;
    @BindView(R.id.like_count_hint)
    ImageView mLikeCountHint;
    @BindView(R.id.like_count_container)
    LinearLayout mLikeCountContainer;
    @BindView(R.id.views_count_hint)
    ImageView mViewsCountHint;
    @BindView(R.id.views_count_container)
    LinearLayout mViewsCountContainer;
    @BindView(R.id.download_count_hint)
    ImageView mDownloadCountHint;
    @BindView(R.id.download_count_container)
    LinearLayout mDownloadCountContainer;
    @BindView(R.id.location_hint)
    ImageView mLocationHint;
    @BindView(R.id.exif_model_hint)
    ImageView mExifModelHint;
    @BindView(R.id.like_progress_bar)
    ProgressBar mLikeProgressBar;

    @BindString(R.string.msg_please_login)
    String mMsgPleaseLogin;

    private long mDownloadingStartTimeMillis;
    private boolean mIsCompleteAnimationPending = false;
    private boolean mIsDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    @Override
    public void initData() {
        Photo photo = getIntent().getParcelableExtra(ARG_PHOTO);

        mPhotoColorId = Color.parseColor(photo.color);
        mPhotoColor = new ColorDrawable(mPhotoColorId);

        mViewModel = ViewModelProviders.of(this).get(PhotoDetailViewModel.class);
        mViewModel.setPhoto(photo);
    }

    @Override
    public void initView() {
        initContent(mViewModel.getPhoto().getValue());
        loadPhoto(mViewModel.getPhoto().getValue());
        //initIcon(mViewModel.getPhotoData());

        mViewModel.getPhoto(mViewModel.getPhoto().getValue().id).observe(this, new ResourceObserver<Resource<Photo>, Photo>(this) {
            @Override
            protected void onSuccess(Photo photo) {
                mViewModel.setPhoto(photo);
                updateContent(photo);
            }
        });

        if (AuthHelper.isLogin()) {
            mCollectBtn.setVisibility(View.VISIBLE);
        } else {
            mCollectBtn.setVisibility(View.GONE);
        }
    }

    private void loadPhoto(Photo photo) {
        ViewGroup.LayoutParams lp = mPhotoView.getLayoutParams();
        lp.width = ViewUtil.getScreenSize(this)[0];
        lp.height = lp.width * photo.height / photo.width;
        mPhotoView.setLayoutParams(lp);

        ImgUtil.loadImg(this, mPhotoView, mPhotoColor, photo.urls.small);
    }

    private void initContent(Photo photo) {
        if (photo.description != null) {
            mDescription.setText(photo.description);
            mDescription.setVisibility(View.VISIBLE);

            mDescription.getViewTreeObserver().
                    addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mDescription.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int height = mDescription.getMeasuredHeight();

                            ValueAnimator animator = AnimHelper.createDropDown(mDescription, 0, height);
                            animator.setDuration(800).start();
                        }
                    });

        } else {
            mDescription.setVisibility(View.GONE);
        }

        if (photo.location != null) {
            mLocation.setText(photo.location.title);
        }
        if (photo.exif != null) {
            mExifModel.setText(photo.exif.model);

            mExposureTime.setText(photo.exif.exposure_time);
            mAperture.setText(photo.exif.aperture);
            mFocalLength.setText(photo.exif.focal_length);
            mIso.setText(photo.exif.iso + "");
        }

        String time;
        if (photo.created_at != null) {
            time = DateUtil.GmtFormat(photo.created_at);
        } else {
            time = DateUtil.GmtFormat(photo.updated_at);
        }
        mTime.setText(time);
        mSize.setText(photo.width + " x " + photo.height);

        ImgUtil.loadImg(PhotoActivity.this, mAvatar, photo.user.profile_image.large);
        mUsername.setText(photo.user.username);
        mColor.setText(photo.color);

        if (photo.current_user_collections.size() > 0) {
            mCollectBtn.setText(R.string.collected);
            ViewUtil.setDrawableStart(mCollectBtn, mIcMark);
        } else {
            ViewUtil.setDrawableStart(mCollectBtn, mIcMarkBorder);
        }

        DownloadInfo downloadInfo = new DownloadInfo(photo, null);
        if(DownloadService.isDownloading(downloadInfo)){
            showDownloadAnim();
        }
    }

    private void updateContent(Photo photo) {
        Animation scaleInAnimation = AnimationUtils.loadAnimation(PhotoActivity.this, R.anim.scale_left_in);
        Animation alphaInAnimation = AnimationUtils.loadAnimation(PhotoActivity.this, R.anim.alpha_in);

        mLikeCount.startAnimation(scaleInAnimation);
        mViewsCount.startAnimation(scaleInAnimation);
        mDownloadCount.startAnimation(scaleInAnimation);
        mLocation.startAnimation(alphaInAnimation);
        mExifModel.startAnimation(alphaInAnimation);

        mLikeCount.setText(photo.likes + "");
        mViewsCount.setText(photo.views + "");
        mDownloadCount.setText(photo.downloads + "");


        if (photo.location != null) {
            mLocation.setText(photo.location.title);
        }
        if (photo.exif != null) {
            mExifModel.setText(photo.exif.model);

            mExposureTime.setText(photo.exif.exposure_time);
            mAperture.setText(photo.exif.aperture);
            mFocalLength.setText(photo.exif.focal_length);
            mIso.setText(photo.exif.iso + "");
        }
    }

    private void initIcon(Photo photo) {
        if (photo.liked_by_user) {
            mLikeCountHint.setImageDrawable(ViewUtil.tintDrawable(mIcFavorite, mPhotoColorId));
        } else {
            mLikeCountHint.setImageDrawable(ViewUtil.tintDrawable(mIcFavoriteBorder, mPhotoColorId));
        }
        mViewsCountHint.setImageDrawable(ViewUtil.tintDrawable(mIcVisibility, mPhotoColorId));
        mDownloadCountHint.setImageDrawable(ViewUtil.tintDrawable(mIcDownload, mPhotoColorId));

        mLocationHint.setImageDrawable(ViewUtil.tintDrawable(mIcPlace, mPhotoColorId));
        ViewUtil.setDrawableStart(mTime, ViewUtil.tintDrawable(mIcTime, mPhotoColorId));
        mExifModelHint.setImageDrawable(ViewUtil.tintDrawable(mIcCamera, mPhotoColorId));

        mExifContainerHint.setImageDrawable(ViewUtil.tintDrawable(mIcArrowDown, mPhotoColorId));

        ViewUtil.setDrawableStart(mSize, ViewUtil.tintDrawable(mIcStraighten, mPhotoColorId));
        ViewUtil.setDrawableStart(mColor, ViewUtil.tintDrawable(mIcPalette, mPhotoColorId));

        ViewUtil.setDrawableEnd(mUsername, ViewUtil.tintDrawable(mIcPersonOutline, mPhotoColorId));
    }

    @OnClick({R.id.like_count_container, R.id.download_count_container, R.id.show_exif_btn, R.id.user_container, R.id.collect_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like_count_container:
                mLikeCountHint.setVisibility(View.GONE);
                mLikeProgressBar.setVisibility(View.VISIBLE);

                if (AuthHelper.isLogin()) {
                    mViewModel.likePhoto(mViewModel.getPhoto().getValue()).observe(this, new ResourceObserver<Resource<LikePhoto>, LikePhoto>(this) {
                        @Override
                        protected void onSuccess(LikePhoto likePhoto) {
                            if (likePhoto.photo.liked_by_user) {
                                mLikeCountHint.setImageDrawable(mIcFavorite);
                            } else {
                                mLikeCountHint.setImageDrawable(mIcFavoriteBorder);
                            }

                            mViewModel.setPhoto(likePhoto.photo);
                            mLikeCount.setText(likePhoto.photo.likes + "");

                            mLikeCountHint.animate()
                                    .rotationBy(360)
                                    .setDuration(300)
                                    .start();

                            onPhotoChange();
                        }

                        @Override
                        protected void onFinal() {
                            super.onFinal();

                            mLikeProgressBar.setVisibility(View.GONE);
                            mLikeCountHint.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    ToastUtil.showShort(this, mMsgPleaseLogin);
                }

                break;
            case R.id.download_count_container:
                if(mIsDownloading){
                    NavHelper.toDownload(this);
                    return;
                }

                mViewModel.getDownloadLink(mViewModel.getPhoto().getValue().id).observe(this, new ResourceObserver<Resource<PhotoDownloadLink>, PhotoDownloadLink>(this) {
                    @Override
                    protected void onSuccess(PhotoDownloadLink link) {
                        DownloadInfo downloadInfo = new DownloadInfo(mViewModel.getPhoto().getValue(), link.url);
                        NavHelper.downloadPhoto(PhotoActivity.this, downloadInfo, DownloadService.ACTION_START);

                        showDownloadAnim();
                    }
                });
                break;
            case R.id.show_exif_btn:
                int height = ViewUtil.getViewSize(mExifContainer)[1];

                if (mExifContainer.getVisibility() == View.VISIBLE) {
                    mExifContainerHint.animate().rotation(0);

                    ValueAnimator animator = AnimHelper.createDropDown(mExifContainer, height, 0);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mExifContainer.setVisibility(View.GONE);
                        }
                    });
                    animator.start();
                } else {
                    mExifContainer.setVisibility(View.VISIBLE);
                    mExifContainerHint.animate().rotation(180);

                    ValueAnimator animator = AnimHelper.createDropDown(mExifContainer, 0, height);
                    animator.start();
                }

                break;
            case R.id.user_container:
                NavHelper.toUser(this, mViewModel.getPhoto().getValue().user, mAvatar);

                break;
            case R.id.collect_btn:
                if (AuthHelper.isLogin()) {
                    NavHelper.collectPhoto(getSupportFragmentManager(), mViewModel.getPhoto().getValue());
                } else {
                    ToastUtil.showShort(this, mMsgPleaseLogin);
                }
                break;
        }
    }

    private void showDownloadAnim() {
        if (mIsCompleteAnimationPending) {
            return;
        }
        if (mIsDownloading) {
            final long delayMillis = 2666 - ((System.currentTimeMillis() - mDownloadingStartTimeMillis) % 2666);
            mDownloadCountHint.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swapAnimation(R.drawable.avd_downloading_finish);
                    mIsCompleteAnimationPending = false;
                }
            }, delayMillis);
            mIsCompleteAnimationPending = true;
        } else {
            swapAnimation(R.drawable.avd_downloading_begin);
            mDownloadingStartTimeMillis = System.currentTimeMillis();
        }
        mIsDownloading = !mIsDownloading;
    }

    private void swapAnimation(@DrawableRes int drawableResId) {
        final Drawable avd = AnimatedVectorDrawableCompat.create(this, drawableResId);
        mDownloadCountHint.setImageDrawable(avd);
        ((Animatable) avd).start();
    }

    private void onPhotoChange() {
        EventBus.getDefault().post(new PhotoChangeEvent(mViewModel.getPhoto().getValue()));
    }
}
