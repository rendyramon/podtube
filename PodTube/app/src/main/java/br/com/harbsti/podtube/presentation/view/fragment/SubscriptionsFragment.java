package br.com.harbsti.podtube.presentation.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.data.repository.impl.youtubedataapi.SubscriptionYoutubeDataApiRepository;
import br.com.harbsti.podtube.domain.mapper.ChannelDomainMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.usecase.impl.SubscriptionUseCaseImpl;
import br.com.harbsti.podtube.presentation.helper.AuthHelper;
import br.com.harbsti.podtube.presentation.helper.ExceptionHelper;
import br.com.harbsti.podtube.presentation.mapper.ChannelSearchResultViewMapper;
import br.com.harbsti.podtube.presentation.mapper.ChannelViewMapper;
import br.com.harbsti.podtube.presentation.model.Channel;
import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;
import br.com.harbsti.podtube.presentation.presenter.SubscriptionsPresenter;
import br.com.harbsti.podtube.presentation.presenter.impl.SubscriptionsPresenterImpl;
import br.com.harbsti.podtube.presentation.view.SubscriptionsView;
import br.com.harbsti.podtube.presentation.view.activity.Henson;
import br.com.harbsti.podtube.presentation.view.adapter.ChannelsAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcosharbs on 29/11/16.
 */

public class SubscriptionsFragment extends Fragment implements SubscriptionsView, ChannelsAdapter.OnChannelsAdapter {

    @InjectView(R.id.channels_recyclerview) RecyclerView channelsRecyclerView;
    @InjectView(R.id.channels_swiperefresh) SwipeRefreshLayout channelSwipeRefresh;
    private SubscriptionsPresenter subscriptionsPresenter;
    private ChannelsAdapter channelsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscriptions, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        channelsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initPresenter();
        channelsAdapter = null;
        subscriptionsPresenter.onCreate();
        channelSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                channelsAdapter = null;
                subscriptionsPresenter.getSubscriptions(null);
            }
        });
    }

    private void initPresenter() {
        subscriptionsPresenter = new SubscriptionsPresenterImpl(this,
                new SubscriptionUseCaseImpl(new SubscriptionYoutubeDataApiRepository(AuthHelper.getYoutubeClient(getActivity())),
                        new ChannelSearchResultDomainMapper(new ChannelDomainMapper())),
                new ChannelSearchResultViewMapper(new ChannelViewMapper()),
                Schedulers.newThread(),
                AndroidSchedulers.mainThread());
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptionsPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        subscriptionsPresenter.onResume();
    }

    @Override
    public void onSubscriptionsLoaded(ChannelSearchResult channelSearchResult) {
        if (channelsAdapter == null) {
            channelsAdapter = new ChannelsAdapter(getActivity(), channelSearchResult, this);
            channelsRecyclerView.setAdapter(channelsAdapter);
        } else {
            channelsAdapter.appendChannelResult(channelSearchResult);
        }
        channelSwipeRefresh.setRefreshing(false);
    }

    @Override
    public void onError(Throwable throwable) {
        channelSwipeRefresh.setRefreshing(false);
        ExceptionHelper.handleThrowable(getActivity(), throwable);
    }

    @Override
    public void onLoadMore(String offset) {
        subscriptionsPresenter.getSubscriptions(offset);
    }

    @Override
    public void onItemSelect(Channel channel) {
        Intent intent = Henson.with(getActivity())
                .gotoChannelDetailActivity()
                .channel(channel)
                .build();
        startActivity(intent);
    }

}
