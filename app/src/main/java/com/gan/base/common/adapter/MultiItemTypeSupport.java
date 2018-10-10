package com.gan.base.common.adapter;

/**
 * 文件描述 为了使adpater支持多布局的
 * 创建人：ganyf
 * 创建时间：2018/2/5
 */


public interface MultiItemTypeSupport<T> {
    int getItemViewType(int position, T t);
    int getViewTypeCount();
    int getItemLayoutResIdByViewType(int type);
}
