package com.gan.base.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.gan.base.R;
import com.gan.base.databinding.ItemMovieBinding;
import com.gan.base.net.networks.NetWorks;
import com.gan.base.net.requestbean.BaseRequest4List;
import com.gan.base.net.requestbean.MovieInfo;
import com.gan.base.net.subscribers.RecycleviewSubscriber;
import com.gan.base.util.ToastUtil;
import com.gan.base.view.GyPullHeadView;
import com.gan.gyrecyclerview.GyRecycleView;
import com.gan.gyrecyclerview.wrapper.HeaderAndFooterWrapper;


/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/7/20
 */
public class DataBindingActivity extends BaseBindingGyRecycleviewActivity<MovieInfo, ItemMovieBinding> {

    @Override
    protected void afterView() {
        super.afterView();
        setTitle("MVVM模式加载数据");
        recycleView.setPullHeaderView(new GyPullHeadView(this));
        //设置没有更多数据提示语显示模式
        recycleView.setFootNodataViewMode(HeaderAndFooterWrapper.NodataFootViewMode.ALWAYS_VISIBLE);
        //用于自定义初次正在加载的布局，不传，将使用默认
        //commonLayout.setFirstLoadView(new View(this));
        ViewGroup errView = (ViewGroup) View.inflate(this, R.layout.layout_exception_view, null);
        //用于自定义错误布局（目前无数据布局也是同一个），不传，将使用默认
        recycleView.setExceptionView(errView);
        recycleView.setCanMore(true);
        recycleView.setPullRefreshEnable(true);
        recycleView.addHeaderView(R.layout.item_recyclerview_head);

        recycleView.setOnItemClickListener(new GyRecycleView.ItemClickListener() {
            @Override
            public void onClick(View view, RecyclerView.ViewHolder holder, int position) {
                //特别注意：如果添加了头布局 position的位置实际是包含头布局的，所以需要减掉头布局的个数然后才能获取到在dataList的位置，如果不包含头布局，postion的位置是实际数据的位置
                ToastUtil.ToastCenter(dataAllList.get(position - 1).title);
            }

            @Override
            public void onLongClick(View view, RecyclerView.ViewHolder holder, int position) {

            }
        });
    }

    @Override
    public boolean setRecyclerViewField() {
        return false;
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_movie;
    }

    @Override
    protected void doItemUI(ItemMovieBinding binding, MovieInfo o) {
        binding.setModel(o);
        binding.executePendingBindings();
    }


    @Override
    protected BaseRequest4List getNetRequest() {
        return new BaseRequest4List();
    }

    @Override
    protected String getNoDataString() {
        return "暂无消息";
    }

    @Override
    protected int getNoDataDrawable() {
        return R.drawable.no_data;
    }

    @Override
    protected void getNetData(RecycleviewSubscriber subscriber, BaseRequest4List request) {
        NetWorks.getInstance().inTheaters(subscriber, request);
    }

    @Override
    public void onAfterRefreshCompleteMask() {

    }

    @Override
    public void onBeforeRefreshMask() {

    }
}
