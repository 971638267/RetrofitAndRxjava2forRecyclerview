package com.gan.base.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gan.base.R;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/4/27
 */
public class GyPullHeadView extends RelativeLayout implements PtrUIHandler {
    private TextView tv_before;
    private View vg_in;
    private TextView tv_after;

    public GyPullHeadView(Context context) {
        super(context);
        initViews(null);
    }

    public GyPullHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.gyrecyclerview_head, this);
        tv_before = (TextView) header.findViewById(R.id.tv_before);
        vg_in = header.findViewById(R.id.vg_in);
        tv_after = (TextView) header.findViewById(R.id.tv_after);
    }


    public GyPullHeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        tv_before.setVisibility(VISIBLE);
        vg_in.setVisibility(GONE);
        tv_after.setVisibility(GONE);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        tv_before.setVisibility(VISIBLE);
        vg_in.setVisibility(GONE);
        tv_after.setVisibility(GONE);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        tv_before.setVisibility(GONE);
        vg_in.setVisibility(VISIBLE);
        tv_after.setVisibility(GONE);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        tv_before.setVisibility(GONE);
        vg_in.setVisibility(GONE);
        tv_after.setVisibility(VISIBLE);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }
}
