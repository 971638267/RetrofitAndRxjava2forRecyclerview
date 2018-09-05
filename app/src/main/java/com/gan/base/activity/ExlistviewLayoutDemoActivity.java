package com.gan.base.activity;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gan.base.R;
import com.gan.base.util.ToastUtil;
import com.gan.base.view.GyPullHeadView;
import com.gan.gyrecyclerview.BaseMultiItemRecyclerViewAdapter;
import com.gan.gyrecyclerview.BaseRecyclerViewAdapter;
import com.gan.gyrecyclerview.GyRecycleView;
import com.gan.gyrecyclerview.MultiItemTypeSupport;
import com.gan.gyrecyclerview.base.ViewHolder;
import com.gan.gyrecyclerview.wrapper.HeaderAndFooterWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 文件描述
 * 创建人：ganyf
 * 创建时间：2018/2/13
 */

public class ExlistviewLayoutDemoActivity extends BaseActivity implements GyRecycleView.RefreshLoadMoreListener {
    GyRecycleView gyRecycleView;
    private boolean isFirstIn = true;
    public List<String> dataList = new ArrayList<String>();

    @Override
    protected int getContentView() {
        return R.layout.activity_exlistview;
    }

    @Override
    protected void afterView() {

    }

    int haseRefresh = 0;
    int limitRefresh = new Random().nextInt(5);

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                haseRefresh++;
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < 10; i++) {
                    list.add("我是测试数据" + i);
                }
                //模拟异常
                if (haseRefresh > limitRefresh) {
                    gyRecycleView.setDateRefreshErr("网络异常", new GyRecycleView.NoDataCallBack() {
                        @Override
                        public void refreshNodata() {
                            Toast.makeText(ExlistviewLayoutDemoActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    if (limitRefresh % 3 != 0) {
                        Toast.makeText(ExlistviewLayoutDemoActivity.this, "数据加载成功" + list.size(), Toast.LENGTH_SHORT).show();
                        gyRecycleView.setDateRefresh(dataList, list, "暂无数据");
                    } else {
                        gyRecycleView.setDateRefresh(dataList, null, "暂无数据");
                    }
                    loop = 0;
                }

            }
        }, 2000);
    }

    int loop = 0;
    int limitLoadErr = new Random().nextInt(5);
    int limitLoadMore = new Random().nextInt(5);

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loop++;
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < 5; i++) {
                    list.add("我是追加的数据" + loop + i);
                }
                if (loop > limitLoadErr) {
                    Toast.makeText(ExlistviewLayoutDemoActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    gyRecycleView.setDateLoadMoreErr();
                } else {
                    Toast.makeText(ExlistviewLayoutDemoActivity.this, "加载更多成功", Toast.LENGTH_SHORT).show();
                    gyRecycleView.setDateLoadMore(dataList, list, loop <= limitLoadMore);
                }
            }
        }, 2000);
    }


    @Override
    public void onAfterRefreshCompleteMask() {
        //用于自定义遮罩 ,结合 （gyListViewLayout.refresh();//没有正在加载提示图标）方式用，比较好
        //Toast.makeText(this,"onAfterRefreshCompleteMask",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBeforeRefreshMask() {
        //用于自定义遮罩 ,结合 （gyListViewLayout.refresh();//没有正在加载提示图标）方式用，比较好
        // Toast.makeText(this,"onBeforeRefreshMask",Toast.LENGTH_SHORT).show();
    }

    /**
     * 首次进入刷新
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstIn) {
            initView();
            if (gyRecycleView != null) {
                //gyRecycleView.firstLoadingView("数据加载中...");//正在加载的图标放在中部（可只定义）
                gyRecycleView.auotRefresh();//正在加载的图标放在头部（自定义头部）
                //gyRecycleView.refresh();//没有正在加载提示图标
            }
            isFirstIn = false;
        }
    }

    private void initView() {

        gyRecycleView = (GyRecycleView) findViewById(R.id.gy_listview);
        gyRecycleView.setRefreshLoadMoreListener(this);

        //处于错误页或者无数据缺省页的刷新模式
        gyRecycleView.setRefreshMode(GyRecycleView.RefreshMode.PULL);

        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        gyRecycleView.setLayoutManager(layoutManager);

        //使用的adpter必须继承自BaseRecyclerViewAdapter
        gyRecycleView.setAdapter(new BaseMultiItemRecyclerViewAdapter<String>(this, dataList) {
            @NonNull
            @Override
            protected MultiItemTypeSupport<String> getMultiItemTypeSupport() {
                return new MultiItemTypeSupport<String>() {
                    @Override
                    public int getItemViewType(int position, String s) {
                        int type = 3000;
                        switch (position % 3) {
                            case 0:
                                type = 1000;
                                break;
                            case 1:
                                type = 2000;
                                break;
                            default:
                                break;
                        }
                        return type;
                    }

                    @Override
                    public int getItemLayoutResIdByViewType(int viewType) {
                        int layoutId = -1;
                        switch (viewType) {
                            case 1000:
                                layoutId = R.layout.item_test_gy_listview;
                                break;
                            case 2000:
                                layoutId = R.layout.item_test_gy_listview2;
                                break;
                            default:
                                layoutId = R.layout.item_test_gy_listview3;
                                break;
                        }
                        return layoutId;
                    }

                    @Override
                    public boolean isClickEnabled(int viewType) {
                        boolean ok = false;
                        switch (viewType) {
                            case 1000:
                                ok = true;
                                break;
                            case 2000:
                                ok = true;
                                break;
                            default:
                                break;
                        }
                        return ok;
                    }
                };
            }

            @Override
            public void doItem(ViewHolder holder, String o, int postion) {
                holder.setText(R.id.tv_test, o);
            }
        });
        gyRecycleView.setPullHeaderView(new GyPullHeadView(this));
        //设置没有更多数据提示语显示模式
        gyRecycleView.setFootNodataViewMode(HeaderAndFooterWrapper.NodataFootViewMode.ALWAYS_VISIBLE);
        //用于自定义初次正在加载的布局，不传，将使用默认
        //commonLayout.setFirstLoadView(new View(this));
        ViewGroup errView = (ViewGroup) View.inflate(this, R.layout.layout_exception_view, null);
        //用于自定义错误布局（目前无数据布局也是同一个），不传，将使用默认
        gyRecycleView.setExceptionView(errView);
        gyRecycleView.setCanMore(true);
        gyRecycleView.setPullRefreshEnable(true);
        gyRecycleView.addHeaderView(R.layout.item_recyclerview_head);
        gyRecycleView.setOnItemClickListener(new GyRecycleView.ItemClickListener() {
            @Override
            public void onClick(View view, RecyclerView.ViewHolder holder, int position) {
                //特别注意：如果添加了头布局 position的位置实际是包含头布局的，所以需要减掉头布局的个数然后才能获取到在dataList的位置，如果不包含头布局，postion的位置是实际数据的位置
                ToastUtil.ToastCenter(position + "==" + dataList.get(position - 1));
            }

            @Override
            public void onLongClick(View view, RecyclerView.ViewHolder holder, int position) {

            }
        });

    }

}
