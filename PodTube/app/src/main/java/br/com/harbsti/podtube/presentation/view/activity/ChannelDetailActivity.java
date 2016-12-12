package br.com.harbsti.podtube.presentation.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.text.DecimalFormat;

import br.com.harbsti.podtube.BuildConfig;
import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.data.repository.impl.youtubedataapi.ChannelYoutubeDataApiRepository;
import br.com.harbsti.podtube.data.repository.impl.youtubedataapi.SubscriptionYoutubeDataApiRepository;
import br.com.harbsti.podtube.domain.mapper.ChannelDataMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelDomainMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.mapper.VideoDomainMapper;
import br.com.harbsti.podtube.domain.mapper.VideoSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.usecase.impl.ChannelUseCaseImpl;
import br.com.harbsti.podtube.domain.usecase.impl.SubscriptionUseCaseImpl;
import br.com.harbsti.podtube.presentation.helper.AuthHelper;
import br.com.harbsti.podtube.presentation.helper.ExceptionHelper;
import br.com.harbsti.podtube.presentation.mapper.ChannelViewMapper;
import br.com.harbsti.podtube.presentation.mapper.VideoSearchResultViewMapper;
import br.com.harbsti.podtube.presentation.mapper.VideoViewMapper;
import br.com.harbsti.podtube.presentation.model.Channel;
import br.com.harbsti.podtube.presentation.model.Video;
import br.com.harbsti.podtube.presentation.model.VideoSearchResult;
import br.com.harbsti.podtube.presentation.presenter.ChannelDetailPresenter;
import br.com.harbsti.podtube.presentation.presenter.impl.ChannelDetailPresenterImpl;
import br.com.harbsti.podtube.presentation.service.impl.DownloadServiceImpl;
import br.com.harbsti.podtube.presentation.view.ChannelDetailView;
import br.com.harbsti.podtube.presentation.view.adapter.VideosAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class ChannelDetailActivity extends AppCompatActivity implements ChannelDetailView, VideosAdapter.OnVideosAdapter {

    @InjectView(R.id.image_channel_banner) SimpleDraweeView imageChannelBanner;
    @InjectView(R.id.image_channel) SimpleDraweeView imageChannel;
    @InjectView(R.id.label_channel_title) TextView labelChannelTitle;
    @InjectView(R.id.label_channel_description) TextView labelChannelDescription;
    @InjectView(R.id.label_channel_subscribers) TextView labelChannelSubscribers;
    @InjectView(R.id.videos_recyclerview) RecyclerView videosRecyclerview;
    @InjectExtra Channel channel;
    private VideosAdapter videosAdapter;
    private ChannelDetailPresenter channelDetailPresenter;
    private MenuItem menuItemChannelSubscribe;
    private MenuItem menuItemChannelUnsubscribe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_detail);
        ButterKnife.inject(this);
        Dart.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(channel.title());
        videosRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        initPresenter();
        channelDetailPresenter.onCreate();
    }

    private void initPresenter() {
        channelDetailPresenter = new ChannelDetailPresenterImpl(this,
                new ChannelUseCaseImpl(new ChannelYoutubeDataApiRepository(AuthHelper.getYoutubeClient(this)),
                        new ChannelDataMapper(),
                        new ChannelDomainMapper(),
                        new ChannelSearchResultDomainMapper(new ChannelDomainMapper()),
                        new VideoSearchResultDomainMapper(new VideoDomainMapper())),
                new SubscriptionUseCaseImpl(new SubscriptionYoutubeDataApiRepository(AuthHelper.getYoutubeClient(this)),
                        new ChannelSearchResultDomainMapper(new ChannelDomainMapper())),
                new ChannelViewMapper(),
                new VideoSearchResultViewMapper(new VideoViewMapper()),
                new br.com.harbsti.podtube.presentation.mapper.ChannelDomainMapper(),
                Schedulers.newThread(),
                AndroidSchedulers.mainThread());
    }

    @Override
    protected void onPause() {
        super.onPause();
        channelDetailPresenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        channelDetailPresenter.onResume();
    }

    @Override
    public void onChannelDetailLoaded(Channel channel) {
        this.channel = channel;
        imageChannelBanner.setImageURI(ImageRequestBuilder.newBuilderWithSource(Uri.parse(channel.bannerImage())).build().getSourceUri());
        imageChannel.setImageURI(ImageRequestBuilder.newBuilderWithSource(Uri.parse(channel.image())).build().getSourceUri());
        labelChannelTitle.setText(channel.title());
        if(channel.description() == null || channel.description().isEmpty()){
            labelChannelDescription.setVisibility(View.GONE);
        }else{
            labelChannelDescription.setText(channel.description());
        }
        labelChannelSubscribers.setText(new DecimalFormat("#,###,###").format(channel.subscribersCount()) + " " + getString(R.string.label_subscriptions));
    }

    @Override
    public void onChannelVideosLoaded(VideoSearchResult videoSearchResult) {
        if(videosAdapter == null){
            videosAdapter = new VideosAdapter(this, videoSearchResult, channel.videosCount(), this);
            videosRecyclerview.setAdapter(videosAdapter);
        }else{
            videosAdapter.appendVideoResult(videoSearchResult);
        }
    }

    @Override
    public Channel getChannelExtra() {
        return channel;
    }

    @Override
    public void onUserSubscription(Channel channel) {
        this.channel = channel;
        if(this.channel.subscriptionId() != null && ! this.channel.subscriptionId().isEmpty()){
            menuItemChannelUnsubscribe.setVisible(true);
            menuItemChannelUnsubscribe.getIcon().setAlpha(255);
        }else{
            menuItemChannelSubscribe.setVisible(true);
            menuItemChannelSubscribe.getIcon().setAlpha(100);
        }
    }

    @Override
    public void onSubscriptionAdded(Channel channel) {
        this.channel = channel;
        menuItemChannelSubscribe.setVisible(false);
        menuItemChannelUnsubscribe.setVisible(true);
        menuItemChannelUnsubscribe.getIcon().setAlpha(255);
    }

    @Override
    public void onSubscriptionRemoved(Channel channel) {
        this.channel = channel;
        menuItemChannelUnsubscribe.setVisible(false);
        menuItemChannelSubscribe.setVisible(true);
        menuItemChannelSubscribe.getIcon().setAlpha(100);
    }

    @Override
    public void onError(Throwable throwable) {
        ExceptionHelper.handleThrowable(this, throwable);
    }

    @Override
    public void onLoadMore(String offset) {
        channelDetailPresenter.getChannelVideos(channel.uploadsPlaylistName(), offset);
    }

    @Override
    public void onDownload(Video video) {
        Toast.makeText(this, R.string.label_download_msg, Toast.LENGTH_SHORT).show();
        Bundle extras = new Bundle();
        extras.putParcelable("VIDEO", video);
        Intent downloadService = new Intent(this, DownloadServiceImpl.class);
        downloadService.putExtras(extras);
        startService(downloadService);
    }

    @Override
    public void onViewMovie(Video video) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(this,
                BuildConfig.YOUTUBE_KEY,
                video.id(),
                0,
                true,
                true);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_detail_menu, menu);
        menuItemChannelSubscribe = menu.findItem(R.id.channel_subscribe);
        menuItemChannelUnsubscribe = menu.findItem(R.id.channel_unsubscribe);
        menuItemChannelSubscribe.setVisible(false);
        menuItemChannelUnsubscribe.setVisible(false);

        menuItemChannelSubscribe.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                channelDetailPresenter.addSubscription(channel.id());
                return false;
            }
        });

        menuItemChannelUnsubscribe.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                channelDetailPresenter.deleteSubscription(channel.subscriptionId());
                return false;
            }
        });

        return true;
    }

}
