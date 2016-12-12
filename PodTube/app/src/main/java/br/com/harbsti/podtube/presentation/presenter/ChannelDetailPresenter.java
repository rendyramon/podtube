package br.com.harbsti.podtube.presentation.presenter;

import br.com.harbsti.podtube.presentation.model.Channel;

/**
 * Created by marcosharbs on 06/12/16.
 */

public interface ChannelDetailPresenter extends Presenter {

    void getChannelDetail(Channel channel);

    void getChannelVideos(String playlistId, String offset);

    void getUserSubscription(String channelId);

    void addSubscription(String channelId);

    void deleteSubscription(String subscriptionId);

}
