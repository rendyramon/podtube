package br.com.harbsti.podtube.presentation.view;

import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;

/**
 * Created by marcosharbs on 05/12/16.
 */

public interface SearchChannelView extends View {

    void onChannelsLoaded(ChannelSearchResult channelSearchResult);

}
