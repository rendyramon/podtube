package br.com.harbsti.podtube.presentation.presenter;

import br.com.harbsti.podtube.presentation.model.Download;

/**
 * Created by marcosharbs on 09/12/16.
 */

public interface MediaPlayerPresenter extends Presenter {

    void play(Download download);

    void pausePlayer();

    void playPlayer();

    void stopPlayer();

}
