package br.com.harbsti.podtube.presentation.view;

import br.com.harbsti.podtube.presentation.model.Channel;
import br.com.harbsti.podtube.presentation.model.VideoSearchResult;

/**
 * Created by marcosharbs on 06/12/16.
 */

public interface ChannelDetailView extends View {

    void onChannelDetailLoaded(Channel channel);

    void onChannelVideosLoaded(VideoSearchResult videoSearchResult);

    Channel getChannelExtra();

    void onUserSubscription(Channel channel);

    void onSubscriptionAdded(Channel channel);

    void onSubscriptionRemoved(Channel channel);

}
