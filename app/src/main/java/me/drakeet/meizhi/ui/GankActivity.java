package me.drakeet.meizhi.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.umeng.analytics.MobclickAgent;
import java.util.Date;
import me.drakeet.meizhi.R;
import me.drakeet.meizhi.adapter.GankPagerAdapter;
import me.drakeet.meizhi.event.LoveBus;
import me.drakeet.meizhi.event.OnKeyBackClickEvent;
import me.drakeet.meizhi.ui.base.ToolbarActivity;

public class GankActivity extends ToolbarActivity {

    public static final String EXTRA_GANK_DATE = "gank_date";

    @Bind(R.id.pager) ViewPager mViewPager;
    @Bind(R.id.tabLayout) TabLayout mTabLayout;

    GankPagerAdapter mPagerAdapter;

    @Override protected int getLayoutResource() {
        return R.layout.activity_gank;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        initViewPager();
        initTabLayout();
    }

    private void initViewPager() {
        Date gankDate = (Date) getIntent().getSerializableExtra(EXTRA_GANK_DATE);
        mPagerAdapter = new GankPagerAdapter(getSupportFragmentManager(), gankDate);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
    }

    private void initTabLayout() {
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            mTabLayout.addTab(mTabLayout.newTab());
        }
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { hideOrShowToolbar(); }
        else { hideOrShowToolbar(); }
    }

    @Override protected void hideOrShowToolbar() {
        View toolbar = findViewById(R.id.toolbar_with_indicator);
        toolbar.animate()
            .translationY(mIsHidden ? 0 : -mToolbar.getHeight())
            .setInterpolator(new DecelerateInterpolator(2))
            .start();
        mIsHidden = !mIsHidden;
        if (mIsHidden) {
            mViewPager.setTag(mViewPager.getPaddingTop());
            mViewPager.setPadding(0, 0, 0, 0);
        }
        else {
            mViewPager.setPadding(0, (int) mViewPager.getTag(), 0, 0);
            mViewPager.setTag(null);
        }
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                    LoveBus.getLovelySeat().post(new OnKeyBackClickEvent());
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gank, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LoveBus.getLovelySeat().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        LoveBus.getLovelySeat().unregister(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}