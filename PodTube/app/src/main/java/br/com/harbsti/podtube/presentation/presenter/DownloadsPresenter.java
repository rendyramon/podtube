package br.com.harbsti.podtube.presentation.presenter;

import br.com.harbsti.podtube.presentation.model.Download;

/**
 * Created by marcosharbs on 07/12/16.
 */

public interface DownloadsPresenter extends Presenter {

    void getDownloads();

    void deleteDownload(Download download);

}
