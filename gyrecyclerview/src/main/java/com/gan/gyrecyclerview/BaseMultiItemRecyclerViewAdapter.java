package com.gan.gyrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gan.gyrecyclerview.base.ViewHolder;

import java.util.List;


public abstract class BaseMultiItemRecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T> {

    protected MultiItemTypeSupport<T> mulitSupport;

    public BaseMultiItemRecyclerViewAdapter(Context context, List<T> datas) {
        super(context, -1, datas);
        this.mulitSupport = getMultiItemTypeSupport();
    }

    @NonNull
    protected abstract MultiItemTypeSupport<T> getMultiItemTypeSupport();


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mulitSupport.getItemLayoutResIdByViewType(viewType) <= 0) {
            throw new RuntimeException("布局不能为空");
        }
        ViewHolder viewHolder = ViewHolder.createViewHolder(mContext, parent, mulitSupport.getItemLayoutResIdByViewType(viewType));
        setListener(parent, viewHolder, viewType);
        return viewHolder;
    }

    @Override
    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!mulitSupport.isClickEnabled(viewType)) {
            return;
        }
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mulitSupport.getItemViewType(position, mDatas.get(position));
    }
}
