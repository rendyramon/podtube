package br.com.harbsti.podtube.presentation.presenter.impl;

import br.com.harbsti.podtube.domain.usecase.ChannelUseCase;
import br.com.harbsti.podtube.presentation.mapper.ChannelSearchResultViewMapper;
import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;
import br.com.harbsti.podtube.presentation.presenter.SearchChannelPresenter;
import br.com.harbsti.podtube.presentation.view.SearchChannelView;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by marcosharbs on 05/12/16.
 */

public class SearchChannelPresenterImpl implements SearchChannelPresenter {

    private SearchChannelView searchChannelView;
    private ChannelUseCase channelUseCase;
    private ChannelSearchResultViewMapper channelSearchResultViewMapper;
    private Scheduler subscriberSchduler;
    private Scheduler observerScheduler;

    public SearchChannelPresenterImpl(SearchChannelView searchChannelView,
                                      ChannelUseCase channelUseCase,
                                      ChannelSearchResultViewMapper channelSearchResultViewMapper,
                                      Scheduler subscriberSchduler,
                                      Scheduler observerScheduler) {

        this.searchChannelView = searchChannelView;
        this.channelUseCase = channelUseCase;
        this.channelSearchResultViewMapper = channelSearchResultViewMapper;
        this.subscriberSchduler = subscriberSchduler;
        this.observerScheduler = observerScheduler;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onPause() {}

    @Override
    public void onResume() {}

    @Override
    public void searchChannels(String term, String offset) {
        channelUseCase.getChannels(term, offset)
                .compose(channelSearchResultViewMapper.getTransformer())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<ChannelSearchResult>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        searchChannelView.onError(e);
                    }

                    @Override
                    public void onNext(ChannelSearchResult channelSearchResult) {
                        searchChannelView.onChannelsLoaded(channelSearchResult);
                    }
                });
    }

}
