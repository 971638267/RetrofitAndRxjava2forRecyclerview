package com.gan.base.common.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/2/5
 */


public abstract class MultiItemCommonAdapter<T> extends CommonAdapter<T> {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemCommonAdapter(Context context, List<T> datas) {
        super(context, datas);
        mMultiItemTypeSupport = getMultiItemTypeSupport();
    }

    @NonNull
    protected abstract MultiItemTypeSupport<T> getMultiItemTypeSupport();

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeSupport.getItemViewType(position, datas.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return mMultiItemTypeSupport.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (getCount() > 0) {
            int viewType = getItemViewType(position);

            final T t = datas.get(position);
            ViewHolder holder;
            if (convertView == null) {
                if (getViewTypeCount() <= 0) {
                    throw new RuntimeException("布局种类必须大于等于1");

                } else {
                    if (mMultiItemTypeSupport.getItemLayoutResIdByViewType(viewType) == 0) {
                        throw new RuntimeException("布局为空！");
                    } else {
                        holder = ViewHolder.createViewHolder(context, viewGroup, mMultiItemTypeSupport.getItemLayoutResIdByViewType(viewType));
                        convertView = holder.getConvertView();
                    }
                }

            } else {
                holder = (CommonAdapter.ViewHolder) convertView.getTag();
            }
            doItem(holder, t, position);
            return convertView;
        }
        return null;
    }

    @Override
    public int getItemLayoutResId() {
        return 0;
    }
}
