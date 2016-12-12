package br.com.harbsti.podtube.presentation.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeStandalonePlayer;

import java.util.List;

import br.com.harbsti.podtube.BuildConfig;
import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.data.helper.ServiceHelper;
import br.com.harbsti.podtube.data.mapper.DownloadDataRealmMapper;
import br.com.harbsti.podtube.data.mapper.DownloadRealmDataMapper;
import br.com.harbsti.podtube.data.repository.impl.realm.DownloadRealmRepository;
import br.com.harbsti.podtube.data.repository.impl.retrofit.YoutubeInMp3Repository;
import br.com.harbsti.podtube.data.repository.impl.retrofit.service.Mp3Service;
import br.com.harbsti.podtube.domain.mapper.DownloadDataMapper;
import br.com.harbsti.podtube.domain.mapper.DownloadDomainMapper;
import br.com.harbsti.podtube.domain.usecase.impl.DownloadVideoUseCaseImpl;
import br.com.harbsti.podtube.presentation.broadcast.DownloadCompletedReceiver;
import br.com.harbsti.podtube.presentation.broadcast.DownloadDeletedReceiver;
import br.com.harbsti.podtube.presentation.broadcast.DownloadStartedReceiver;
import br.com.harbsti.podtube.presentation.helper.ExceptionHelper;
import br.com.harbsti.podtube.presentation.mapper.DownloadViewMapper;
import br.com.harbsti.podtube.presentation.model.Download;
import br.com.harbsti.podtube.presentation.presenter.DownloadsPresenter;
import br.com.harbsti.podtube.presentation.presenter.impl.DownloadsPresenterImpl;
import br.com.harbsti.podtube.presentation.view.DownloadsView;
import br.com.harbsti.podtube.presentation.view.PlayerView;
import br.com.harbsti.podtube.presentation.view.adapter.DownloadsAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by marcosharbs on 07/12/16.
 */

public class DownloadsFragment extends Fragment implements DownloadsView,
                                                            DownloadsAdapter.OnDownloadsAdapter,
                                                            DownloadCompletedReceiver.OnDownloadCompleted,
                                                            DownloadStartedReceiver.OnDownloadStarted,
                                                            DownloadDeletedReceiver.OnDownloadDeleted {

    @InjectView(R.id.downloads_recyclerview) RecyclerView downloadsRecyclerView;
    private DownloadsPresenter downloadsPresenter;
    private DownloadsAdapter downloadsAdapter;
    private DownloadCompletedReceiver downloadCompletedReceiver;
    private IntentFilter downloadCompletedIntentFilter;
    private DownloadStartedReceiver downloadStartedReceiver;
    private IntentFilter downloadStartedIntentFilter;
    private DownloadDeletedReceiver downloadDeletedReceiver;
    private IntentFilter downloadDeletedIntentFilter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.downloads, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        downloadsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initPresenter();
        downloadsAdapter = null;
        downloadsPresenter.onCreate();
        downloadCompletedReceiver = new DownloadCompletedReceiver(this);
        downloadCompletedIntentFilter = new IntentFilter(DownloadCompletedReceiver.FILTER);
        downloadStartedReceiver = new DownloadStartedReceiver(this);
        downloadStartedIntentFilter = new IntentFilter(DownloadStartedReceiver.FILTER);
        downloadDeletedReceiver = new DownloadDeletedReceiver(this);
        downloadDeletedIntentFilter = new IntentFilter(DownloadDeletedReceiver.FILTER);
        getActivity().registerReceiver(downloadCompletedReceiver, downloadCompletedIntentFilter);
        getActivity().registerReceiver(downloadStartedReceiver, downloadStartedIntentFilter);
        getActivity().registerReceiver(downloadDeletedReceiver, downloadDeletedIntentFilter);
    }

    private void initPresenter() {
        downloadsPresenter = new DownloadsPresenterImpl(this,
                new DownloadVideoUseCaseImpl(new YoutubeInMp3Repository(
                        ServiceHelper.createRetrfitService(Mp3Service.class,
                                "http://www.youtubeinmp3.com")),
                        new DownloadRealmRepository(new DownloadRealmDataMapper(),
                                new DownloadDataRealmMapper()),
                        new DownloadDataMapper(),
                        new DownloadDomainMapper()),
                new DownloadViewMapper(),
                Schedulers.newThread(),
                AndroidSchedulers.mainThread());
    }

    @Override
    public void onPause() {
        super.onPause();
        downloadsPresenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadsPresenter.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(downloadCompletedReceiver);
        getActivity().unregisterReceiver(downloadStartedReceiver);
        getActivity().unregisterReceiver(downloadDeletedReceiver);
    }

    @Override
    public void onDownloadsLoaded(List<Download> downloads) {
        downloadsAdapter = new DownloadsAdapter(getActivity(), downloads, this);
        downloadsRecyclerView.setAdapter(downloadsAdapter);
    }

    @Override
    public void onDownloadRemoved() {
        downloadsPresenter.getDownloads();
    }

    @Override
    public void onError(Throwable throwable) {
        ExceptionHelper.handleThrowable(getActivity(), throwable);
    }

    @Override
    public void onDownloadCompleted() {
        downloadsPresenter.getDownloads();
    }

    @Override
    public void onDownloadStarted() {
        downloadsPresenter.getDownloads();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPlay(Download download) {
        PlayerView playerView = (PlayerView) getActivity();
        playerView.playVideo(download);
    }

    @Override
    public void onDelete(final Download download) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.label_delete)
                .setMessage(R.string.label_delete_msg)
                .setPositiveButton(R.string.label_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadsPresenter.deleteDownload(download);
                    }
                })
                .setNegativeButton(R.string.label_no, null)
                .show();
    }

    @Override
    public void onViewMovie(Download download) {
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                BuildConfig.YOUTUBE_KEY,
                download.id(),
                0,
                true,
                true);
        startActivity(intent);
    }

    @Override
    public void onDownloadDeleted() {
        downloadsPresenter.getDownloads();
    }
}
