package com.gan.gyrecyclerview.inter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.gan.gyrecyclerview.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/7/30
 */
public interface IbaseAdapterHelper<T> {
    List<T> getData();

    void setOnItemClickListener(OnItemClickListener onItemClickListener);

    interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }
}
