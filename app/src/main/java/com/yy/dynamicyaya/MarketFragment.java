package com.yy.dynamicyaya;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.orhanobut.logger.Logger;
import com.yy.saltonframework.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2016/12/19 15:33
 * Time: 15:33
 * Description:
 */
public class MarketFragment extends BaseFragment implements BGAOnItemChildClickListener {

    //    @BindView(R.id.mv_RecyclerView)
    RecyclerView mvRecyclerView;
    //    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    PluginItemAdapter recycleViewAdapter;
    private ArrayList<PluginBean> videosList = new ArrayList<>();
    private String areaCode;

    protected boolean refresh;
    protected int lastVisibleItem;
    protected boolean hasMore = true;
    protected static final int SIZE = 20;
    protected int mOffset = 0;

    public static MarketFragment getInstance(String areaCode) {
        MarketFragment mvViewPagerItemFragment = new MarketFragment();
        Bundle bundle = new Bundle();
        bundle.putString("areaCode", areaCode);
        mvViewPagerItemFragment.setArguments(bundle);
        return mvViewPagerItemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle bundle = getArguments();
//        areaCode = bundle.getString("areaCode");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fm_market);
        mvRecyclerView = getViewById(R.id.recyclerView);
        swipeRefreshLayout = getViewById(R.id.swipeRefreshLayout);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        initView();
    }

    @Override
    protected void onUserVisible() {

    }


    private void initView() {
        recycleViewAdapter = new PluginItemAdapter(mvRecyclerView);
        recycleViewAdapter.setOnItemChildClickListener(this);
        mvRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mvRecyclerView.setAdapter(recycleViewAdapter);
        mvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                Logger.e("onScrollStateChanged");
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItem + 1 == recycleViewAdapter.getItemCount()) && hasMore) {
//                    getData(mOffset + 1, SIZE, areaCode);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                Logger.e("onScrolled");
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources()
                        .getDisplayMetrics()));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh = true;
                videosList.clear();
                mOffset = 0;
                getData(mOffset, SIZE, areaCode);
//                Logger.e("onRefresh");
//                dismissLoading();
                //加载数据
            }
        });
        getData(mOffset, SIZE, areaCode);
    }

    public void getData(int offset, int size, String areaCode) {
        showLoading();
        ArrayList<PluginBean> plugins = new ArrayList<>();
        plugins.add(new PluginBean(1, "wifi密码查看1", "http://mobile.d.appchina.com/McDonald/e/2238868/0/0/0/1482394249791/package_2.1482394249791", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(2, "wifi密码查看2", "http://mobile.d.appchina.com/McDonald/e/2238868/0/0/0/1482394249791/package_2.1482394249791", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(3, "wifi密码查看3", "http://mobile.d.appchina.com/McDonald/e/2238868/0/0/0/1482394249791/package_2.1482394249791", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        plugins.add(new PluginBean(4, "wifi密码查看4", "http://mobile.d.appchina.com/McDonald/e/2238868/0/0/0/1482394249791/package_2.1482394249791", "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png"));
        setData(plugins);

//        OkHttpManager.getOkHttpManager().asyncGet(URLProviderUtil.getMVListUrl(areaCode, offset, size), new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                showSnake(e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response != null) {
//                    try {
//                        MVListBean mvListBean = new Gson().fromJson(response.body().string(), MVListBean.class);
//                        Message msg = Message.obtain();
//                        msg.obj = mvListBean.getVideos();
//                        msg.what = 101;
//                        mHandler.sendMessage(msg);
//
//                        //setData(mvListBean.getVideos());
//                    } catch (JsonSyntaxException e) {
//                        e.printStackTrace();
//                        showSnake(e.getLocalizedMessage());
//                    }
//                } else {
//                    showSnake("");
//                }
//            }
//        });
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    ArrayList<PluginBean> PluginBeanArrayList = (ArrayList<PluginBean>) msg.obj;
                    setData(PluginBeanArrayList);
                    break;
            }
        }
    };

    public void setData(List<PluginBean> videoList) {
        dismissLoading();
        if (refresh) {
            refresh = false;
            mOffset = 0;
            videosList.clear();
        }
        if (videoList == null || videoList.size() == 0) {
            hasMore = false;
        } else {
            hasMore = true;
            int pos = videosList.size() - 1;
            videosList.addAll(videoList);
            recycleViewAdapter.notifyItemRangeChanged(pos, videoList.size());
            mOffset += videoList.size();
        }
        recycleViewAdapter.addNewData(videoList);
    }

    public void showLoading() {
        swipeRefreshLayout.setRefreshing(true);
    }

    public void dismissLoading() {
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showSnake(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onItemChildClick(ViewGroup parent, final View childView, final int position) {
        String text = ((BootstrapButton) childView.findViewById(R.id.btn_download)).getText().toString().trim();
        Logger.i(text);
        if (text.equals(getString(R.string.install))) {     //
        } else {
        }

    }

//    private void download(final int position) {
//        PluginBean pluginBean = recycleViewAdapter.getItem(position);
//        FileDownloaderModel model = DownloaderManager.getInstance().addTask(pluginBean.getPluginUrl());
//        DownloaderManager.getInstance().startTask(model.getId(), new FileDownloaderCallback() {
//            public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed, int progress) {
//                Logger.i("下载进度：" + progress);
//                recycleViewAdapter.updateProgress(position,progress+"");
//            }
//
//            public void onFinish(int downloadId, String path) {
//                Logger.i("下载完成,安装模块");
//
//            }
//        });
//    }

//
//    private void download() {
//        Subscription subscription = RxDownload.getInstance()
//                .download(recycleViewAdapter.getItem(position) == null ? "" : recycleViewAdapter.getItem(position).getPluginUrl(), recycleViewAdapter.getItem(position) == null ? "" : recycleViewAdapter.getItem(position).getName() + ".apk", null)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<DownloadStatus>() {
//                    @Override
//                    public void onCompleted() {
//                        //下载完成
//                        ((BootstrapButton) childView.findViewById(R.id.btn_download)).setText("打开");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //下载出错
//                        ((BootstrapButton) childView.findViewById(R.id.btn_download)).setText("重试");
//                    }
//
//                    @Override
//                    public void onNext(final DownloadStatus status) {
//                        //下载状态
//                        ((BootstrapButton) childView.findViewById(R.id.btn_download)).setText("" + status.getPercent());
//                    }
//                });
//    }
}


