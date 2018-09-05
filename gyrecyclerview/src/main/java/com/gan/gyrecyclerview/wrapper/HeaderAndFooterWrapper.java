package com.gan.gyrecyclerview.wrapper;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gan.gyrecyclerview.R;
import com.gan.gyrecyclerview.base.ViewHolder;
import com.gan.gyrecyclerview.utils.WrapperUtils;


public class HeaderAndFooterWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private SparseArrayCompat<Integer> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<Integer> mFootViews = new SparseArrayCompat<>();

    private RecyclerView.Adapter mInnerAdapter;
    private RecyclerView recycleView;
    private int nodataMoreMode = NodataFootViewMode.OUT_VISIBLE;
    private OnLoadMoreListener mOnLoadMoreListener;
    private View mLoadMoreView;
    private View mNodataView;
    private ViewGroup mFooterView;
    private int loadMoreViewId = -1;
    private CharSequence mNodataStr = null;

    public HeaderAndFooterWrapper(RecyclerView.Adapter adapter) {
        mInnerAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            ViewGroup mHeaderView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mHeaderViews.get(viewType), parent,
                    false);
            ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), mHeaderView);
            return holder;

        } else if (mFootViews.get(viewType) != null) {
            ViewGroup mFootView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(mFootViews.get(viewType), parent,
                    false);
            if (mFootViews.get(viewType) == loadMoreViewId) {
                mFooterView = mFootView;
                if (mLoadMoreView == null) {
                    mLoadMoreView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gyrecyclerview_foot_default_loading, parent,
                            false);
                }
                if (mNodataView == null) {
                    mNodataView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gyrecyclerview_nodata_view, parent,
                            false);
                    if (!TextUtils.isEmpty(mNodataStr)) {
                        TextView tv = (TextView) mNodataView.findViewById(R.id.nodata_text);
                        if (tv != null) {
                            tv.setText(mNodataStr);
                        }
                    }
                }
                mFooterView.addView(mLoadMoreView);
                mFooterView.addView(mNodataView);
                mNodataView.setVisibility(View.GONE);
            }
            ViewHolder holder = ViewHolder.createViewHolder(parent.getContext(), mFootView);
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position)) {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeaderViewPos(position)) {
            return;
        }
        if (isFooterViewPos(position)) {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemCount() {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                } else if (mFootViews.get(viewType) != null) {
                    return layoutManager.getSpanCount();
                }
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position)) {
            WrapperUtils.setFullSpan(holder);
            if (isLoadmore(position)) {
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMoreRequested();
                }
            }
        }
    }

    private boolean isLoadmore(int position) {
        int noViewType = getItemViewType(position);
        int index = -1;
        for (int i = 0; i < mFootViews.size(); i++) {
            if (mFootViews.valueAt(i) == loadMoreViewId) {
                index = i;
                break;
            }
        }
        int loadMoreType = -1;
        if (index != -1) {
            loadMoreType = mFootViews.keyAt(index);
        }
        return noViewType == loadMoreType;
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public void addHeaderView(int viewId) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, viewId);
    }

    public void addFootView(int viewId) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, viewId);
    }

    public void addFootView(int viewId, boolean isLoadMoreView) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, viewId);
        if (isLoadMoreView) {
            this.loadMoreViewId = viewId;
        }
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFootViews.size();
    }


    public void setRecycerView(RecyclerView recycleView) {
        this.recycleView = recycleView;
    }

    public void setLoadMoreMode(int nodataMoreMode) {
        this.nodataMoreMode = nodataMoreMode;
    }

    public HeaderAndFooterWrapper<T> setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }

    public void setLoadingMsg(CharSequence msg, boolean hidden) {
        if (mLoadMoreView != null) {
            mLoadMoreView.findViewById(R.id.pb_loading).setVisibility(hidden ? View.GONE : View.VISIBLE);
            TextView tv = (TextView) mLoadMoreView.findViewById(R.id.loading_text);
            tv.setText(msg);

        }
    }

    public void setNodataMsg(CharSequence msg) {
        this.mNodataStr = msg;

    }

    public void setFootCanLoad(boolean footCanload) {
        if (footCanload) {//如果可以加载更多布局
            if (mNodataView != null) {
                mFooterView.setVisibility(View.VISIBLE);
                mNodataView.setVisibility(View.GONE);
                mLoadMoreView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mNodataView != null) {
                if (nodataMoreMode == NodataFootViewMode.ALWAYS_VISIBLE) {
                    mFooterView.setVisibility(View.VISIBLE);
                    mNodataView.setVisibility(View.VISIBLE);
                } else if (nodataMoreMode == NodataFootViewMode.HIDDEN) {
                    mFooterView.setVisibility(View.GONE);
                    mNodataView.setVisibility(View.GONE);
                    mLoadMoreView.setVisibility(View.GONE);
                } else {
                    recycleView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!notFullScreen()) {
                                mFooterView.setVisibility(View.VISIBLE);
                                mNodataView.setVisibility(View.VISIBLE);
                            } else {
                                mFooterView.setVisibility(View.GONE);
                                mNodataView.setVisibility(View.GONE);
                                mLoadMoreView.setVisibility(View.GONE);
                            }
                        }
                    }, 100);

                }
            }
        }

    }

    public boolean notFullScreen() {
        return !recycleView.canScrollVertically(1) && !recycleView.canScrollVertically(-1);
    }


    public class NodataFootViewMode {
        /**
         * 当所有数据全部展示时，显示没有更多数据尾部布局提示
         */
        public static final int ALWAYS_VISIBLE = 0;

        /**
         * 当所有数据全部展示时，不满一屏不显示没有更多数据尾部布局提示,满一屏时显示
         */
        public static final int OUT_VISIBLE = 1;

        /**
         * 当所有数据全部展示时，不显示没有更多数据尾部布局提示
         */
        public static final int HIDDEN = 2;
    }
}
