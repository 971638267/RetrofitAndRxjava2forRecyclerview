package com.gan.gyrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gan.gyrecyclerview.wrapper.HeaderAndFooterWrapper;
import com.gan.gyrecyclerview.wrapper.LoadMoreWrapper;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * 用途: 自定义recycleview实现下拉刷新和自动加载
 * 创建者:ganyufei
 * 时间: 2017/2/8
 */

public class GyRecycleView<T> extends LinearLayout implements PtrUIHandler {
    private PtrFrameLayout mPtrFrame;
    private int defaltErrIconId = -1;
    private int defaltNodataIconId = -1;

    private RecyclerView recyclerView;
    //private SwipeRefreshLayout swipeRfl;
    // private LinearLayoutManager layoutManager;
    private PtrHandler mRefreshListener;
    private CommonAdapter mAdapter;
    private RefreshLoadMoreListener mRefreshLoadMoreListner;//下拉和加载更多监听
    private ItemClickListener itemClickListener;//item点击监听
    private ViewGroup mExceptView;
    private ViewGroup mLoadingView;
    private boolean hasMore = false;//是否还有更多数据加载
    private boolean canMore = true;//是否可以加载更多
    private boolean isCanRefresh = true;//是否可以刷新更多
    private boolean isRefresh = false;//正在刷新
    private boolean isLoadMore = false;//正在加载更多
    private LoadMoreWrapper mLoadMoreWrapper;//为了实现加载更多footview

    private ImageView exceptIv;//异常图片控件
    private TextView exceptTv;//异常内容文本控件

    private ProgressBar loadingIv;//正在加载图片控件
    private TextView loadingTv;//正在加载文本控件
    private RecyclerView.ItemAnimator itemAnimator;
    private HeaderAndFooterWrapper<T> headerWrapper;//头布局
    private boolean addHead = false;//是否添加头布局
    private int headViewId;
    private View headView;
    private View errInnerView;
    private TextView clickUpdateHintTv;

    public GyRecycleView(Context context) {
        super(context);
    }

    public GyRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GyRecyclerview);
            defaltErrIconId = typedArray.getResourceId(R.styleable.GyRecyclerview_defalt_err_icon, -1);
            defaltNodataIconId = typedArray.getResourceId(R.styleable.GyRecyclerview_defalt_nodata_icon, -1);
            typedArray.recycle();
        }
        View rootView = View.inflate(context, R.layout.layout_gyrecyclerview, this);

        mLoadingView = initLoadingView(context);
        mExceptView = initExceptionView(context);
        mExceptView.setVisibility(View.GONE);
        mPtrFrame = (PtrFrameLayout) rootView.findViewById(R.id.mPtrFrame);
        View header = getHeadForListView();
        //设置头部view
        mPtrFrame.setHeaderView(header);
        if (header instanceof PtrUIHandler) {
            //如果头部实现了PtrUIHandler，则添加到PtrUIHandler组中，使头部也响应状态控制
            mPtrFrame.addPtrUIHandler((PtrUIHandler) header);
        }
        //设置ui状态变化的回调
        mPtrFrame.addPtrUIHandler(this);

        /**
         * 下拉至顶部刷新监听
         */
        mRefreshListener = new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (!isRefresh && !isLoadMore) {
                    refresh();
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                //判断是否可以下拉刷新
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, recyclerView, header);
            }
        };
        mPtrFrame.setPtrHandler(mRefreshListener);
        mPtrFrame.setEnabled(false);

        recyclerView = (RecyclerView) findViewById(R.id.rv_list);
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.setHorizontalScrollBarEnabled(true);
        if (itemAnimator != null) {
            recyclerView.setItemAnimator(itemAnimator);
        } else {
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        recyclerView.setHasFixedSize(true);//不是瀑布流这个将可以优化性能
    }


    public GyRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isRefreshOrLoadmore() {
        return isRefresh || isLoadMore;
    }

    /**
     * 错误提示界面初始化
     *
     * @param context
     * @return
     */
    private ViewGroup initExceptionView(Context context) {
        ViewGroup tmp = (ViewGroup) findViewById(R.id.vg_err);
        if (errInnerView == null) {
            errInnerView = (LinearLayout) View.inflate(context, R.layout.gyrecyclerview_err, null);
            exceptIv = (ImageView) errInnerView.findViewById(R.id.iv_err_img);
            exceptTv = (TextView) errInnerView.findViewById(R.id.tv_err_msg);
            clickUpdateHintTv = (TextView) errInnerView.findViewById(R.id.tv_err_hint);
            tmp.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 点击刷新
                    if (!isRefresh) {
                        // 点击图片刷新
                        if (!isRefresh) {
                            firstLoadingView(null);
                        }
                    }
                }
            });
        }
        tmp.addView(errInnerView);
        return tmp;
    }

    /**
     * 初始化正在加载页面
     *
     * @param context
     * @return
     */
    private ViewGroup initLoadingView(Context context) {
        ViewGroup tmp = (ViewGroup) findViewById(R.id.vg_first_load);
        LinearLayout rootLl = (LinearLayout) View.inflate(context, R.layout.gyrecyclerview_firstload, null);
        loadingIv = (ProgressBar) rootLl.findViewById(R.id.myrecle_load_progress);
        loadingTv = (TextView) rootLl.findViewById(R.id.myrecle_load_msg);
        tmp.addView(rootLl);
        return tmp;
    }

    private View getHeadForListView() {
        if (headView == null) {
            headView = new DefaltHeadForGyRecycerView(getContext());
        }
        return headView;
    }

    /**
     * drawableId 错误提示图片
     * exceptStr 错误提示语
     */
    private void customExceptView(int drawableId, String exceptStr) {
        recyclerView.setVisibility(View.INVISIBLE);
        mExceptView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.INVISIBLE);
        exceptIv.setImageResource(drawableId);
        exceptTv.setText(exceptStr);
        mPtrFrame.setEnabled(false);//出现错误之后，将设定无法下拉，运用点击图片进行刷新
    }

    /**
     * drawableId 正在加载提示图片
     * exceptStr 正在加载提示语
     */
    public void customLoadView(String exceptStr) {
        recyclerView.setVisibility(View.INVISIBLE);
        mLoadingView.setVisibility(View.VISIBLE);
        mExceptView.setVisibility(View.INVISIBLE);
        if (null != exceptStr) {
            loadingTv.setText(exceptStr);
        }
        mPtrFrame.setEnabled(false);
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    public void setAdapter(CommonAdapter adapter) {
        if (adapter != null) {
            this.mAdapter = adapter;
            if (canMore) {//是否可以加载更多
                mLoadMoreWrapper = new LoadMoreWrapper(mAdapter);
                // mLoadMoreWrapper.setLoadMoreView(false);//是否有加载更多默认有
                mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        /**
                         * 无论水平还是垂直
                         */
                        if (hasMore && !isLoadMore && !isRefresh && canMore) {
                            isLoadMore = true;
                            loadMore();
                        }
                    }
                });
                if (addHead) {
                    this.headerWrapper = new HeaderAndFooterWrapper<T>(mLoadMoreWrapper);
                    headerWrapper.addHeaderView(headViewId);
                    recyclerView.setAdapter(headerWrapper);
                } else {
                    recyclerView.setAdapter(mLoadMoreWrapper);
                }

            } else {
                if (addHead) {
                    this.headerWrapper = new HeaderAndFooterWrapper<T>(mAdapter);
                    headerWrapper.addHeaderView(headViewId);
                    recyclerView.setAdapter(headerWrapper);
                } else {
                    recyclerView.setAdapter(mAdapter);
                }
            }

            mAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(view, holder, position);
                    }
                }

                @Override
                public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                    if (itemClickListener != null) {
                        itemClickListener.onLongClick(view, holder, position);
                    }
                    return true;
                }
            });
        }
    }

    private void setHasMore(boolean enable) {
        this.hasMore = enable;
        if (mLoadMoreWrapper != null) {
            mLoadMoreWrapper.setFootCanLoad(hasMore);
        }
    }

   /* public boolean isHasMore() {
        return hasMore;
    }

    public boolean isCanMore() {
        return canMore;
    }*/

    public boolean isCanMore() {
        return canMore;
    }

    public void setCanMore(boolean canMore) {
        this.canMore = canMore;
    }

    public void setPullRefreshEnable(boolean enable) {
        isCanRefresh = enable;
        mPtrFrame.setEnabled(enable);
    }

    public boolean getPullRefreshEnable() {
        return mPtrFrame.isEnabled();
    }

    public void loadMore() {
        if (mRefreshLoadMoreListner != null && hasMore && canMore) {
            mRefreshLoadMoreListner.onLoadMore();
        }
    }

    /**
     * 加载更多完毕,为防止频繁网络请求,isLoadMore为false才可再次请求更多数据
     */
    public void setLoadMoreCompleted() {
        isLoadMore = false;
    }

    public void stopRefresh() {
        isRefresh = false;
        mPtrFrame.refreshComplete();
        if (isCanRefresh) {
            mPtrFrame.setEnabled(true);
        }
    }

    public void setRefreshLoadMoreListener(RefreshLoadMoreListener listener) {
        mRefreshLoadMoreListner = listener;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    /**
     * 刷新动作，用于请求网络数据
     */
    public void refresh() {
        mExceptView.setVisibility(View.GONE);
        mPtrFrame.setEnabled(false);
        isRefresh = true;
        if (mRefreshLoadMoreListner != null) {
            mRefreshLoadMoreListner.onBeforeRefreshMask();
            mRefreshLoadMoreListner.onRefresh();
        }
    }

    public void notifyDataSetChanged() {
        Log.d("gyrecyclerview", "height===" + recyclerView.getHeight());
        //firstload布局只能出现一次，所以这里判断如果显示，就隐藏
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            mExceptView.setVisibility(View.INVISIBLE);
            mLoadingView.setVisibility(View.INVISIBLE);
        }
       /* if (mLoadMoreWrapper != null)
            mLoadMoreWrapper.notifyDataSetChanged();
        else
            mAdapter.notifyDataSetChanged();*/
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * 第一次自动加载，不与无数据用同样布局是因为，这里要有动画效果，所以单独一个布局
     */
    public void firstLoadingView(String exceptStr) {
        customLoadView(exceptStr);
        isRefresh = true;
        if (mRefreshLoadMoreListner != null) {
            mRefreshLoadMoreListner.onBeforeRefreshMask();
            mRefreshLoadMoreListner.onRefresh();
        }
    }


    /**
     * 获取刷新数据以后的处理
     *
     * @param actAllList
     * @param tmp
     * @param drawableId 当没有数据时提示图片
     * @param msg        没有数据时提示语
     */
    public void setDateRefresh(List<T> actAllList, List<T> tmp, int drawableId, String msg) {
        actAllList.clear();
        stopRefresh();//如果刷新则停止刷新
        if (tmp == null || tmp.isEmpty()) {
            customExceptView(drawableId, msg);
            setHasMore(false);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            setHasMore(true);
            actAllList.addAll(tmp);
        }
        notifyDataSetChanged();//刷新完毕
    }

    /**
     * 获取加载更多数据的处理
     *
     * @param actAllList
     * @param tmpLoadmore
     */
    public void setDateLoadMore(List<T> actAllList, List<T> tmpLoadmore) {
        if (tmpLoadmore == null || tmpLoadmore.isEmpty()) {
            setHasMore(false);//如果没有更多数据则设置不可加载更多
            setLoadMoreCompleted();//加载完毕
            stopRefresh();//如果刷新则停止刷新
            return;
        }
        setHasMore(true);
        actAllList.addAll(tmpLoadmore);
        setLoadMoreCompleted();//加载完毕
        notifyDataSetChanged();//加载更多完毕
        stopRefresh();//如果刷新则停止刷新
    }

    /**
     * 刷新数据失败
     *
     * @param darwable
     * @param msg
     */
    public void setDateRefreshErr(int darwable, String msg) {
        stopRefresh();//如果刷新则停止刷新
        customExceptView(darwable, msg);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration div) {
        recyclerView.addItemDecoration(div);
    }

    /**
     * 设置item动画效果
     *
     * @param defaultItemAnimator
     */
    public void setItemAnimator(RecyclerView.ItemAnimator defaultItemAnimator) {
        this.itemAnimator = defaultItemAnimator;
        recyclerView.setItemAnimator(itemAnimator);
    }

    /**
     * 添加头布局
     *
     * @param headerViewId
     */
    public void addHeaderView(int headerViewId) {
        addHead = true;
        this.headViewId = headerViewId;
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        isRefresh = true;
        mExceptView.setVisibility(View.GONE);
        if (mRefreshLoadMoreListner != null) {
            mRefreshLoadMoreListner.onBeforeRefreshMask();
            mRefreshLoadMoreListner.onRefresh();
        }
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }

    /**
     * 下拉刷新和自动加载监听
     */
    public interface RefreshLoadMoreListener {
        void onRefresh();

        void onLoadMore();

        void onAfterRefreshCompleteMask();

        void onBeforeRefreshMask();
    }

    public interface ItemClickListener {
        public void onClick(View view, RecyclerView.ViewHolder holder, int position);

        public void onLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

}
