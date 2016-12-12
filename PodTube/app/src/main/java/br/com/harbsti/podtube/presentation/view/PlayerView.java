package br.com.harbsti.podtube.presentation.view;

import android.content.Context;
import android.widget.SeekBar;

import br.com.harbsti.podtube.presentation.model.Download;

/**
 * Created by marcosharbs on 08/12/16.
 */

public interface PlayerView extends View {

    void playVideo(Download download);

    Context getContext();

    SeekBar getPlayerTrack();

    void onShowPlayer();

    void onHidePlayer();

}
