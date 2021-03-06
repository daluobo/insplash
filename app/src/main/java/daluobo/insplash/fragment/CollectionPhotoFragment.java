package daluobo.insplash.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import daluobo.insplash.R;
import daluobo.insplash.adapter.PhotosAdapter;
import daluobo.insplash.fragment.base.BasePhotoFragment;
import daluobo.insplash.helper.ConfigHelper;
import daluobo.insplash.model.net.Collection;
import daluobo.insplash.view.LineDecoration;
import daluobo.insplash.viewmodel.CollectionPhotoViewModel;
import daluobo.insplash.viewmodel.PhotoViewModel;

/**
 * Created by daluobo on 2017/11/29.
 */

public class CollectionPhotoFragment extends BasePhotoFragment{
    public static final String ARG_COLLECTION = "arg_collection";

    public CollectionPhotoFragment() {
    }

    public static CollectionPhotoFragment newInstance(Collection collection) {
        CollectionPhotoFragment fragment = new CollectionPhotoFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_COLLECTION, collection);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        initData();
        initView();
        return view;
    }

    @Override
    public void initData() {
        Collection collection = getArguments().getParcelable(ARG_COLLECTION);
        mViewModel = ViewModelProviders.of(this).get(CollectionPhotoViewModel.class);
        ((CollectionPhotoViewModel) mViewModel).setCollection(collection);

        mAdapter = new PhotosAdapter(getContext(), mViewModel.getData(), this, (PhotoViewModel) mViewModel, getFragmentManager());

    }

    @Override
    public void initView() {
        super.initListView();

        if (ConfigHelper.isCompatView()) {
            mListView.addItemDecoration(new LineDecoration(getContext(), 0, 4, 4));
        }

        onShowRefresh();
        onRefresh();
    }

}
