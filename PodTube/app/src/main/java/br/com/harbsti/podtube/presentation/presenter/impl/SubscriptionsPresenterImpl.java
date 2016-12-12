package br.com.harbsti.podtube.presentation.presenter.impl;

import br.com.harbsti.podtube.domain.usecase.SubscriptionUseCase;
import br.com.harbsti.podtube.presentation.mapper.ChannelSearchResultViewMapper;
import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;
import br.com.harbsti.podtube.presentation.presenter.SubscriptionsPresenter;
import br.com.harbsti.podtube.presentation.view.SubscriptionsView;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class SubscriptionsPresenterImpl implements SubscriptionsPresenter {

    private SubscriptionsView subscriptionsView;
    private SubscriptionUseCase subscriptionUseCase;
    private ChannelSearchResultViewMapper channelSearchResultViewMapper;
    private Scheduler subscriberSchduler;
    private Scheduler observerScheduler;

    public SubscriptionsPresenterImpl(SubscriptionsView subscriptionsView,
                                      SubscriptionUseCase subscriptionUseCase,
                                      ChannelSearchResultViewMapper channelSearchResultViewMapper,
                                      Scheduler subscriberSchduler,
                                      Scheduler observerScheduler) {

        this.subscriptionsView = subscriptionsView;
        this.subscriptionUseCase = subscriptionUseCase;
        this.channelSearchResultViewMapper = channelSearchResultViewMapper;
        this.subscriberSchduler = subscriberSchduler;
        this.observerScheduler = observerScheduler;
    }

    @Override
    public void getSubscriptions(String offset) {
        subscriptionUseCase.getSubscriptions(offset)
                .compose(channelSearchResultViewMapper.getTransformer())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<ChannelSearchResult>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        subscriptionsView.onError(e);
                    }

                    @Override
                    public void onNext(ChannelSearchResult channelSearchResult) {
                        subscriptionsView.onSubscriptionsLoaded(channelSearchResult);
                    }
                });
    }

    @Override
    public void onCreate() {
        getSubscriptions(null);
    }

    @Override
    public void onPause() {}

    @Override
    public void onResume() {}

}
