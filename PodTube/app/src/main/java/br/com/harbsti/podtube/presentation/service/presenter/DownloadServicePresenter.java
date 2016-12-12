package br.com.harbsti.podtube.presentation.service.presenter;

import br.com.harbsti.podtube.presentation.model.Download;
import br.com.harbsti.podtube.presentation.model.Video;

/**
 * Created by marcosharbs on 06/12/16.
 */

public interface DownloadServicePresenter {

    void persistDownload(Video video);

    void downloadVideo(Download download, String dirToSave);

    void deleteDownload(String id);

}
