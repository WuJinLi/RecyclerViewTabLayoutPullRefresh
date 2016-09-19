package com.steven.bk30_recyclerviewtablayoutpullrefresh.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.R;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.adapter.QiushiAdapter;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.decoration.DividerItemDecoration;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.helper.OkHttpClientHelper;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.model.QiushiModel;
import com.steven.bk30_recyclerviewtablayoutpullrefresh.utils.Constant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

public class DummyFragment extends Fragment {
    private Context mContext = null;
    private PtrFrameLayout ptrFrameLayout_fragment;
    private QiushiAdapter adapter = null;
    private List<QiushiModel.ItemsEntity> totalList = new ArrayList<>();
    private ProgressBar progressBar_fragment;
    private int lastVisibleItem = 0;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView_fragment;
    private int tabindex = 0;
    private int curPage = 1;
    private Handler handler = new Handler();
    private String url_string = "";

    public static DummyFragment newInstance(int tabIndex) {
        DummyFragment fragment = new DummyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("tabindex", tabIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle bundle = getArguments();
        tabindex = bundle.getInt("tabindex");
        switch (tabindex) {
            case 0:
                url_string = String.format(Constant.URL_LATEST, curPage);
                break;
            case 1:
                url_string = String.format(Constant.URL_TEXT, curPage);
                break;
            case 2:
                url_string = String.format(Constant.URL_VIDEO, curPage);
                break;
            case 3:
                url_string = String.format(Constant.URL_TEXT, curPage);
                break;
            case 4:
                url_string = String.format(Constant.URL_LATEST, curPage);
                break;
        }
        loadNetworkData();
    }

    private void loadNetworkData() {
        OkHttpClientHelper.getDataAsync(mContext, url_string,
                new Callback() {

                    @Override
                    public void onFailure(Request request, IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "No data!!!", Toast
                                        .LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                String jsonString = body.string();

                                //json解析
                                QiushiModel result_model = parseJsonToQiushiModel(jsonString);
                                final List<QiushiModel.ItemsEntity> list = result_model.getItems();

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressBar_fragment.isShown()) {
                                            progressBar_fragment.setVisibility(View.GONE);
                                        }
                                        if (curPage == 1) {
                                            adapter.reloadListView(list, true);
                                        } else {
                                            adapter.reloadListView(list, false);
                                        }
                                        // 刷新完成，让刷新Loading消失
                                        ptrFrameLayout_fragment.refreshComplete();
                                    }
                                });
                            }
                        }
                    }
                }, "qiushi_latest");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
        ImageView imageView_backtotop = (ImageView) view.findViewById(R.id.imageView_backtotop);
        progressBar_fragment = (ProgressBar) view.findViewById(R.id.progressBar_fragment);
        ptrFrameLayout_fragment = (PtrFrameLayout) view.findViewById(R.id.ptrFrameLayout_fragment);
        recyclerView_fragment = (RecyclerView) view.findViewById(R.id.recyclerView_fragment);

        // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView_fragment.setHasFixedSize(true);

        // 设置一个垂直方向的layout manager
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                false);

        // 设置布局管理器
        recyclerView_fragment.setLayoutManager(linearLayoutManager);

        //设置分割线或者分割空间
        recyclerView_fragment.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.VERTICAL_LIST));

        adapter = new QiushiAdapter(mContext, totalList);
        recyclerView_fragment.setAdapter(adapter);
        recyclerView_fragment.setItemAnimator(new DefaultItemAnimator());

        //利用RecyclerView的滚动监听实现上拉加载下一页
        recyclerView_fragment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == adapter.getItemCount() - 1) {
                    curPage++;
                    loadNetworkData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }

        });

        //使用PtrFrameLayout实现下拉刷新
        //效果1：设置默认的经典的头视图
        PtrClassicDefaultHeader defaultHeader = new PtrClassicDefaultHeader(mContext);


        //效果2：特殊效果，目前只支持英文字符（闪动的文字Header：闪动文字效果的header）
        StoreHouseHeader storeHouseHeader = new StoreHouseHeader(mContext);
        //storeHouseHeader.setPadding(0, 30, 0, 0);
        storeHouseHeader.setBackgroundColor(Color.BLACK);
        storeHouseHeader.setTextColor(Color.WHITE);
        // 文字只能是0-9,a-z不支持中文
        storeHouseHeader.initWithString("loading...");

        //设置头视图
        ptrFrameLayout_fragment.setHeaderView(storeHouseHeader);
        // 绑定UI与刷新状态的监听
        ptrFrameLayout_fragment.addPtrUIHandler(storeHouseHeader);

        // 添加刷新动作监听
        ptrFrameLayout_fragment.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                curPage = 1;
                loadNetworkData();
            }
        });

        imageView_backtotop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_fragment.scrollToPosition(0);
            }
        });

        return view;
    }

    //gson解析
    private QiushiModel parseJsonToQiushiModel(String jsonString) {
        Gson gson = new Gson();
        QiushiModel model = gson.fromJson(jsonString, new TypeToken<QiushiModel>() {
        }.getType());
        return model;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpClientHelper.cancelCall("qiushi_latest");
    }
}
