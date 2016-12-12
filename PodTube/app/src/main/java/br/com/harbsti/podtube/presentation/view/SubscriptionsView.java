package br.com.harbsti.podtube.presentation.view;

import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;

/**
 * Created by marcosharbs on 06/12/16.
 */

public interface SubscriptionsView extends View {

    void onSubscriptionsLoaded(ChannelSearchResult channelSearchResult);

}
