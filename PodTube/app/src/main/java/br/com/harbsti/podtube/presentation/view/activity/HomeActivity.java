package br.com.harbsti.podtube.presentation.view.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.f2prateek.dart.HensonNavigable;

import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.presentation.helper.ExceptionHelper;
import br.com.harbsti.podtube.presentation.helper.MetricsHelper;
import br.com.harbsti.podtube.presentation.model.Download;
import br.com.harbsti.podtube.presentation.presenter.MediaPlayerPresenter;
import br.com.harbsti.podtube.presentation.presenter.impl.MediaPlayerPresenterImpl;
import br.com.harbsti.podtube.presentation.view.PlayerView;
import br.com.harbsti.podtube.presentation.view.fragment.DownloadsFragment;
import br.com.harbsti.podtube.presentation.view.fragment.SubscriptionsFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by marcosharbs on 26/11/16.
 */

@HensonNavigable
public class HomeActivity extends AppCompatActivity implements PlayerView {

    @InjectView(R.id.home_tabs_layout) TabLayout homeTabsLayout;
    @InjectView(R.id.home_tabs_viewpager) ViewPager homeTabsViewpager;
    @InjectView(R.id.text_video_name) TextView textVideoName;
    @InjectView(R.id.layout_media_player) RelativeLayout layoutMediaPlayer;
    @InjectView(R.id.player_track) SeekBar playerTrack;
    private HomeTabsPagerAdapter homeTabsPagerAdapter;
    private MediaPlayerPresenter mediaPlayerPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.actionbar_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setupTabs();
        initPresenter();
    }

    private void initPresenter() {
        mediaPlayerPresenter = new MediaPlayerPresenterImpl(this);
    }

    private void setupTabs() {
        homeTabsPagerAdapter = new HomeTabsPagerAdapter(getSupportFragmentManager());
        homeTabsViewpager.setAdapter(homeTabsPagerAdapter);
        homeTabsLayout.setupWithViewPager(homeTabsViewpager);
        homeTabsViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                homeTabsPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public void playVideo(Download download) {
        mediaPlayerPresenter.play(download);
        textVideoName.setText(download.title());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public SeekBar getPlayerTrack() {
        return playerTrack;
    }

    @Override
    public void onShowPlayer() {
        layoutMediaPlayer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHidePlayer() {
        layoutMediaPlayer.setVisibility(View.GONE);
    }

    @OnClick(R.id.image_pause)
    public void onPlayerPause() {
        mediaPlayerPresenter.pausePlayer();
    }

    @OnClick(R.id.image_play)
    public void onPlayerPlay() {
        mediaPlayerPresenter.playPlayer();
    }

    @OnClick(R.id.image_stop)
    public void onPlayerStop() {
        mediaPlayerPresenter.stopPlayer();
    }

    @Override
    public void onError(Throwable throwable) {
        ExceptionHelper.handleThrowable(this, throwable);
    }

    public class HomeTabsPagerAdapter extends FragmentPagerAdapter {

        private int[] imageResId = {
                R.drawable.ic_heart,
                R.drawable.ic_download
        };

        public HomeTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SubscriptionsFragment();
                case 1:
                    return new DownloadsFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = ContextCompat.getDrawable(HomeActivity.this, imageResId[position]).mutate();
            image.setAlpha(position != homeTabsViewpager.getCurrentItem() ? 100 : 255);
            image.setBounds(0, 0,
                    MetricsHelper.convertDpToPixel(20f, HomeActivity.this),
                    MetricsHelper.convertDpToPixel(20f, HomeActivity.this));
            SpannableString sb = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem menuItemChannelSearch = menu.findItem(R.id.channel_search);

        menuItemChannelSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                startActivity(Henson.with(HomeActivity.this).gotoSearchChannelActivity().build());
                return false;
            }
        });

        return true;
    }

}
