package com.example.android.basepro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Copyright (C) ,2016-2023,Write by AndySong
 *
 * @PackageName : com.example.android.basepro
 * @Description : TODO
 * @Author : SongZhiXiang
 * @Date : 2023/8/15 11:29
 * @Version : 1.0
 */
public abstract class BaseFragment extends Fragment {

    /**
     * Returns the layout id of the current Fragment.
     */
    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @LayoutRes int layoutId = getLayoutId();
        return inflater.inflate(layoutId, container, false);
    }

}
