package br.com.harbsti.podtube.presentation.view.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.f2prateek.dart.HensonNavigable;

import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.data.repository.impl.youtubedataapi.ChannelYoutubeDataApiRepository;
import br.com.harbsti.podtube.domain.mapper.ChannelDataMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelDomainMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.mapper.VideoDomainMapper;
import br.com.harbsti.podtube.domain.mapper.VideoSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.usecase.impl.ChannelUseCaseImpl;
import br.com.harbsti.podtube.presentation.helper.AuthHelper;
import br.com.harbsti.podtube.presentation.helper.ExceptionHelper;
import br.com.harbsti.podtube.presentation.mapper.ChannelSearchResultViewMapper;
import br.com.harbsti.podtube.presentation.mapper.ChannelViewMapper;
import br.com.harbsti.podtube.presentation.presenter.impl.SearchChannelPresenterImpl;
import br.com.harbsti.podtube.presentation.view.adapter.ChannelsAdapter;
import br.com.harbsti.podtube.presentation.model.Channel;
import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;
import br.com.harbsti.podtube.presentation.presenter.SearchChannelPresenter;
import br.com.harbsti.podtube.presentation.view.SearchChannelView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcosharbs on 05/12/16.
 */

@HensonNavigable
public class SearchChannelActivity extends AppCompatActivity implements SearchChannelView, ChannelsAdapter.OnChannelsAdapter, SearchView.OnQueryTextListener {

    @InjectView(R.id.channels_recyclerview) RecyclerView channelsRecyclerView;
    private SearchView searchView;
    private SearchChannelPresenter searchChannelPresenter;
    private ChannelsAdapter channelsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_channel);
        ButterKnife.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        channelsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initPresenter();
        searchChannelPresenter.onCreate();
    }

    private void initPresenter() {
        searchChannelPresenter = new SearchChannelPresenterImpl(this,
                new ChannelUseCaseImpl(new ChannelYoutubeDataApiRepository(AuthHelper.getYoutubeClient(this)),
                        new ChannelDataMapper(),
                        new ChannelDomainMapper(),
                        new ChannelSearchResultDomainMapper(new ChannelDomainMapper()),
                        new VideoSearchResultDomainMapper(new VideoDomainMapper())),
                new ChannelSearchResultViewMapper(new ChannelViewMapper()),
                Schedulers.newThread(),
                AndroidSchedulers.mainThread());
    }

    @Override
    protected void onPause() {
        super.onPause();
        searchChannelPresenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchChannelPresenter.onResume();
    }

    @Override
    public void onChannelsLoaded(ChannelSearchResult channelSearchResult) {
        if(channelsAdapter == null){
            channelsAdapter = new ChannelsAdapter(this, channelSearchResult, this);
            channelsRecyclerView.setAdapter(channelsAdapter);
        }else{
            channelsAdapter.appendChannelResult(channelSearchResult);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        ExceptionHelper.handleThrowable(this, throwable);
    }

    @Override
    public void onLoadMore(String offset) {
        searchChannelPresenter.searchChannels(searchView.getQuery().toString(), offset);
    }

    @Override
    public void onItemSelect(Channel channel) {
        Intent intent = Henson.with(this)
                .gotoChannelDetailActivity()
                .channel(channel)
                .build();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_channel_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.channel_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_channel_hint));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        channelsAdapter = null;
        searchChannelPresenter.searchChannels(s, null);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
