package com.lzy.ninegridview.model.news;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.ninegridview.R;
import com.lzy.ninegridview.model.news.bean.NewsChannel;
import com.lzy.ninegridview.utils.NewsCallBack;
import com.lzy.ninegridview.utils.Urls;
import com.lzy.ninegridview.view.PagerSlidingTabStrip;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewsActivity extends AppCompatActivity {

    @Bind(R.id.tab) PagerSlidingTabStrip tab;
    @Bind(R.id.viewPager) ViewPager viewPager;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        emptyView = View.inflate(this, R.layout.item_empty, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(emptyView, params);

        OkHttpUtils.get(Urls.CHANNEL).tag(this).execute(new NewsCallBack<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    emptyView.setVisibility(View.GONE);
                    JSONArray object = new JSONObject(s).getJSONObject("showapi_res_body").getJSONArray("channelList");
                    Type channelItemType = new TypeToken<List<NewsChannel>>() {}.getType();
                    List<NewsChannel> channelItems = new Gson().fromJson(object.toString(), channelItemType);
                    viewPager.setAdapter(new ChannelAdapter(getSupportFragmentManager(), channelItems));
                    tab.setViewPager(viewPager);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class ChannelAdapter extends FragmentPagerAdapter {
        private List<NewsChannel> channelItems;

        public ChannelAdapter(FragmentManager fm, List<NewsChannel> channelItems) {
            super(fm);
            this.channelItems = channelItems;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return channelItems.get(position).getName();
        }

        @Override
        public Fragment getItem(int position) {
            NewsFragment fragment = new NewsFragment();
            NewsChannel channel = channelItems.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("channelId", channel.getChannelId());
            bundle.putInt("page", 1);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return channelItems.size();
        }
    }
}

