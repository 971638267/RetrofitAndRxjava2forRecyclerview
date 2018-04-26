package com.gan.gyrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by ganyf on 2018/1/24.
 */

public class DefaltHeadForGyRecycerView extends LinearLayout {

    private ProgressBar base_loading_progress;

    public DefaltHeadForGyRecycerView(Context context) {
        super(context);
        initViews(null);
    }

    public DefaltHeadForGyRecycerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public DefaltHeadForGyRecycerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(attrs);
    }

    protected void initViews(AttributeSet attrs) {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.gyrecyclerview_defalt_header, this);
        base_loading_progress = (ProgressBar) header.findViewById(R.id.base_loading_progress);
    }

  /*  @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        setWheelVisible(true);
        setIvMoneyVisible(true);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        setWheelVisible(false);
        setIvMoneyVisible(false);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }

    public void setWheelVisible(boolean visible) {
        if (base_loading_progress != null) {
            if (visible) {
                base_loading_progress.setVisibility(VISIBLE);
                base_loading_progress.spin();
            } else {
                base_loading_progress.setVisibility(GONE);
                base_loading_progress.stopSpinning();
            }
        }
    }

    public void setIvMoneyVisible(boolean visible) {
        if (iv_money != null) {
            if (visible) {
                iv_money.setVisibility(VISIBLE);
            } else {
                iv_money.setVisibility(GONE);
            }
        }
    }*/
}
