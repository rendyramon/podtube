package br.com.harbsti.podtube.domain.usecase;

import br.com.harbsti.podtube.domain.model.Channel;
import br.com.harbsti.podtube.domain.model.ChannelSearchResult;
import br.com.harbsti.podtube.domain.model.VideoSearchResult;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by marcosharbs on 05/12/16.
 */

public interface ChannelUseCase {

    Observable<ChannelSearchResult> getChannels(String term, String offset);

    Observable<Channel> getChannelDetail(Channel channel);

    Observable<VideoSearchResult> getChannelVideos(String playlistId, String offset);

}
