package br.com.harbsti.podtube.presentation.view;

import java.util.List;

import br.com.harbsti.podtube.presentation.model.Download;

/**
 * Created by marcosharbs on 07/12/16.
 */

public interface DownloadsView extends View {

    void onDownloadsLoaded(List<Download> downloads);

    void onDownloadRemoved();

}
