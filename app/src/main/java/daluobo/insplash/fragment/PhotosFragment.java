package daluobo.insplash.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import daluobo.insplash.R;
import daluobo.insplash.adapter.PhotosAdapter;
import daluobo.insplash.base.view.SwipeListFragment;
import daluobo.insplash.event.ViewEvent;
import daluobo.insplash.helper.ConfigHelper;
import daluobo.insplash.helper.PopupMenuHelper;
import daluobo.insplash.model.OptionItem;
import daluobo.insplash.model.net.Photo;
import daluobo.insplash.view.LineDecoration;
import daluobo.insplash.viewmodel.PhotoViewModel;

/**
 * Created by daluobo on 2017/11/9.
 */

public class PhotosFragment extends SwipeListFragment<List<Photo>> {
    protected LayoutInflater mInflater;
    private TextView mPhotoType;
    private TextView mOrderBy;
    private PhotoViewModel mPhotoViewModel;

    @BindView(R.id.header_container)
    FrameLayout mHeaderContainer;
    @BindColor(R.color.colorBg)
    int mColorBg;

    protected PopupMenuHelper.OnMenuItemClickListener mTypeClickListener = new PopupMenuHelper.OnMenuItemClickListener() {
        @Override
        public void onItemClick(OptionItem menuItem) {
            mPhotoViewModel.setCurrentType(menuItem);
        }

        @Override
        public void onDismiss() {

        }
    };

    protected PopupMenuHelper.OnMenuItemClickListener mOrderByClickListener = new PopupMenuHelper.OnMenuItemClickListener() {
        @Override
        public void onItemClick(OptionItem menuItem) {
            mPhotoViewModel.setOrderByType(menuItem);
        }

        @Override
        public void onDismiss() {

        }
    };

    public PhotosFragment() {
    }

    public static PhotosFragment newInstance() {
        PhotosFragment fragment = new PhotosFragment();
        return fragment;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        initData();
        initView();
        return view;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewEvent(ViewEvent event){
        initListView();
    }

    @Override
    public void initData() {
        mInflater = LayoutInflater.from(getContext());

        mViewModel = ViewModelProviders.of(this).get(PhotoViewModel.class);
        mPhotoViewModel = (PhotoViewModel) mViewModel;
        mPhotoViewModel.getCurrentType().observe(this, new Observer<OptionItem>() {
            @Override
            public void onChanged(@Nullable OptionItem menuItem) {
                mPhotoType.setText(menuItem.title);
                ((PhotoViewModel) mViewModel).setType(menuItem.value);

                onShowRefresh();
                onRefresh();
            }
        });
        mPhotoViewModel.getOrderByType().observe(this, new Observer<OptionItem>() {
            @Override
            public void onChanged(@Nullable OptionItem menuItem) {
                mOrderBy.setText(menuItem.title);
                mViewModel.setOrderBy(menuItem.value);

                if (!mSwipeLayout.isRefreshing()) {
                    onShowRefresh();
                    onRefresh();
                }
            }
        });

        mPhotoViewModel.setCurrentType(PopupMenuHelper.getPhotoType().get(0));
        mPhotoViewModel.setOrderByType(PopupMenuHelper.getOrderBy().get(0));
    }

    @Override
    public void initView() {
        View titleView = mInflater.inflate(R.layout.header_photos, null);
        mPhotoType = titleView.findViewById(R.id.photo_type);
        mPhotoType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectType();
            }
        });

        mOrderBy = titleView.findViewById(R.id.order_by);
        mOrderBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectOrderBy();
            }
        });
        mHeaderContainer.addView(titleView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        initListView();
    }

    @Override
    public void initListView() {
        if (ConfigHelper.isCompatView()) {
            mAdapter = new PhotosAdapter(getContext(), mViewModel.getData(), this, (PhotoViewModel) mViewModel, getFragmentManager(), true, 2);
        } else {
            mAdapter = new PhotosAdapter(getContext(), mViewModel.getData(), this, (PhotoViewModel) mViewModel, getFragmentManager());
        }

        super.initListView();

        if (ConfigHelper.isCompatView()) {
            mListView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            mListView.addItemDecoration(new LineDecoration(getContext(), 8, 4, 4));
        }
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void showSelectType() {
        PopupMenuHelper.showPhotoTypeMenu(getContext(), mPhotoType, mPhotoViewModel.getCurrentTypeData(), mTypeClickListener);
    }

    private void showSelectOrderBy() {
        PopupMenuHelper.showOrderByMenu(getContext(), mOrderBy, mPhotoViewModel.getOrderByTypeData(), mOrderByClickListener);
    }
}
