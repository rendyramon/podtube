package br.com.harbsti.podtube.domain.usecase;

import java.util.List;

import br.com.harbsti.podtube.domain.model.Download;
import br.com.harbsti.podtube.domain.model.Video;
import rx.Observable;

/**
 * Created by marcosharbs on 06/12/16.
 */

public interface DownloadVideoUseCase {

    Observable<Download> downloadMp3(Download download, String dirToSave);

    Observable<List<Download>> getDownloads();

    Observable<Download> getDownload(String id);

    Observable<Void> deleteDownload(String id);

    Observable<Download> persist(Download download);

}
