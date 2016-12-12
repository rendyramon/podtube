package br.com.harbsti.podtube.presentation.presenter.impl;

import java.util.List;

import br.com.harbsti.podtube.domain.usecase.DownloadVideoUseCase;
import br.com.harbsti.podtube.presentation.mapper.DownloadViewMapper;
import br.com.harbsti.podtube.presentation.model.Download;
import br.com.harbsti.podtube.presentation.presenter.DownloadsPresenter;
import br.com.harbsti.podtube.presentation.view.DownloadsView;
import rx.Scheduler;
import rx.Subscriber;

/**
 * Created by marcosharbs on 07/12/16.
 */

public class DownloadsPresenterImpl implements DownloadsPresenter {

    private DownloadsView downloadsView;
    private DownloadVideoUseCase downloadVideoUseCase;
    private DownloadViewMapper downloadViewMapper;
    private Scheduler subscriberSchduler;
    private Scheduler observerScheduler;

    public DownloadsPresenterImpl(DownloadsView downloadsView,
                                  DownloadVideoUseCase downloadVideoUseCase,
                                  DownloadViewMapper downloadViewMapper,
                                  Scheduler subscriberSchduler,
                                  Scheduler observerScheduler) {

        this.downloadsView = downloadsView;
        this.downloadVideoUseCase = downloadVideoUseCase;
        this.downloadViewMapper = downloadViewMapper;
        this.subscriberSchduler = subscriberSchduler;
        this.observerScheduler = observerScheduler;
    }

    @Override
    public void getDownloads() {
        downloadVideoUseCase.getDownloads()
                .compose(downloadViewMapper.getTransformerList())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<List<Download>>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        downloadsView.onError(e);
                    }

                    @Override
                    public void onNext(List<Download> downloads) {
                        downloadsView.onDownloadsLoaded(downloads);
                    }
                });
    }

    @Override
    public void deleteDownload(Download download) {
        downloadVideoUseCase.deleteDownload(download.id())
                .subscribeOn(subscriberSchduler)
                .observeOn(observerScheduler)
                .subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        downloadsView.onError(e);
                    }

                    @Override
                    public void onNext(Void aVoid) {
                        downloadsView.onDownloadRemoved();
                    }
                });
    }

    @Override
    public void onCreate() {
        getDownloads();
    }

    @Override
    public void onPause() {}

    @Override
    public void onResume() {}

}
