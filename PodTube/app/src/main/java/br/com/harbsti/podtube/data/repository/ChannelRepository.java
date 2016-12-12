package br.com.harbsti.podtube.data.repository;

import br.com.harbsti.podtube.data.model.Channel;
import br.com.harbsti.podtube.data.model.ChannelSearchResult;
import br.com.harbsti.podtube.data.model.VideoSearchResult;
import rx.Observable;

/**
 * Created by marcosharbs on 05/12/16.
 */

public interface ChannelRepository {

    Observable<ChannelSearchResult> getChannels(String term, String offset);

    Observable<Channel> getChannelDetail(Channel channel);

    Observable<VideoSearchResult> getVideos(String playlistId, String offset);

}
