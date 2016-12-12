package br.com.harbsti.podtube.presentation.presenter.impl;

import br.com.harbsti.podtube.domain.usecase.SubscriptionUseCase;
import br.com.harbsti.podtube.presentation.mapper.ChannelDomainMapper;
import br.com.harbsti.podtube.domain.usecase.ChannelUseCase;
import br.com.harbsti.podtube.presentation.mapper.ChannelViewMapper;
import br.com.harbsti.podtube.presentation.mapper.VideoSearchResultViewMapper;
import br.com.harbsti.podtube.presentation.model.Channel;
import br.com.harbsti.podtube.presentation.model.VideoSearchResult;
import br.com.harbsti.podtube.presentation.presenter.ChannelDetailPresenter;
import br.com.harbsti.podtube.presentation.view.ChannelDetailView;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class ChannelDetailPresenterImpl implements ChannelDetailPresenter {

    private ChannelDetailView channelDetailView;
    private ChannelUseCase channelUseCase;
    private SubscriptionUseCase subscriptionUseCase;
    private ChannelViewMapper channelViewMapper;
    private VideoSearchResultViewMapper videoSearchResultViewMapper;
    private ChannelDomainMapper channelDomainMapper;
    private Scheduler subscriberSchduler;
    private Scheduler observerScheduler;

    public ChannelDetailPresenterImpl(ChannelDetailView channelDetailView,
                                      ChannelUseCase channelUseCase,
                                      SubscriptionUseCase subscriptionUseCase,
                                      ChannelViewMapper channelViewMapper,
                                      VideoSearchResultViewMapper videoSearchResultViewMapper,
                                      ChannelDomainMapper channelDomainMapper,
                                      Scheduler subscriberSchduler,
                                      Scheduler observerScheduler) {

        this.channelDetailView = channelDetailView;
        this.channelUseCase = channelUseCase;
        this.subscriptionUseCase = subscriptionUseCase;
        this.channelViewMapper = channelViewMapper;
        this.videoSearchResultViewMapper = videoSearchResultViewMapper;
        this.channelDomainMapper = channelDomainMapper;
        this.subscriberSchduler = subscriberSchduler;
        this.observerScheduler = observerScheduler;
    }

    @Override
    public void getChannelDetail(Channel channel) {
        channelUseCase.getChannelDetail(channelDomainMapper.transform(channel))
                .compose(channelViewMapper.getTransformer())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<Channel>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        channelDetailView.onError(e);
                    }

                    @Override
                    public void onNext(Channel channel) {
                        channelDetailView.onChannelDetailLoaded(channel);
                        getUserSubscription(channel.id());
                        getChannelVideos(channel.uploadsPlaylistName(), null);
                    }
                });
    }

    @Override
    public void getChannelVideos(String playlistId, String offset) {
        channelUseCase.getChannelVideos(playlistId, offset)
                .compose(videoSearchResultViewMapper.getTransformer())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<VideoSearchResult>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        channelDetailView.onError(e);
                    }

                    @Override
                    public void onNext(VideoSearchResult videoSearchResult) {
                        channelDetailView.onChannelVideosLoaded(videoSearchResult);
                    }
                });
    }

    @Override
    public void getUserSubscription(String channelId) {
        subscriptionUseCase.getUserSubscription(channelId)
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        channelDetailView.onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        Channel channel = channelDetailView.getChannelExtra();
                        channelDetailView.onUserSubscription(Channel.builder()
                                .id(channel.id())
                                .title(channel.title())
                                .description(channel.description())
                                .image(channel.image())
                                .bannerImage(channel.bannerImage())
                                .videosCount(channel.videosCount())
                                .subscribersCount(channel.subscribersCount())
                                .uploadsPlaylistName(channel.uploadsPlaylistName())
                                .subscriptionId(s)
                                .build());
                    }
                });
    }

    @Override
    public void addSubscription(String channelId) {
        subscriptionUseCase.addSubscription(channelId)
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        channelDetailView.onError(e);
                    }

                    @Override
                    public void onNext(String s) {
                        Channel channel = channelDetailView.getChannelExtra();
                        channelDetailView.onSubscriptionAdded(Channel.builder()
                                .id(channel.id())
                                .title(channel.title())
                                .description(channel.description())
                                .image(channel.image())
                                .bannerImage(channel.bannerImage())
                                .videosCount(channel.videosCount())
                                .subscribersCount(channel.subscribersCount())
                                .uploadsPlaylistName(channel.uploadsPlaylistName())
                                .subscriptionId(s)
                                .build());
                    }
                });
    }

    @Override
    public void deleteSubscription(String subscriptionId) {
        subscriptionUseCase.deleteSubscription(channelDetailView.getChannelExtra().subscriptionId())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        channelDetailView.onError(e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Channel channel = channelDetailView.getChannelExtra();
                        channelDetailView.onSubscriptionRemoved(Channel.builder()
                                .id(channel.id())
                                .title(channel.title())
                                .description(channel.description())
                                .image(channel.image())
                                .bannerImage(channel.bannerImage())
                                .videosCount(channel.videosCount())
                                .subscribersCount(channel.subscribersCount())
                                .uploadsPlaylistName(channel.uploadsPlaylistName())
                                .subscriptionId(null)
                                .build());
                    }
                });
    }

    @Override
    public void onCreate() {
        getChannelDetail(channelDetailView.getChannelExtra());
    }

    @Override
    public void onPause() {}

    @Override
    public void onResume() {}

}
