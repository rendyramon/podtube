package br.com.harbsti.podtube.domain.usecase.impl;

import br.com.harbsti.podtube.data.repository.ChannelRepository;
import br.com.harbsti.podtube.domain.mapper.ChannelDataMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelDomainMapper;
import br.com.harbsti.podtube.domain.mapper.ChannelSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.mapper.VideoSearchResultDomainMapper;
import br.com.harbsti.podtube.domain.model.Channel;
import br.com.harbsti.podtube.domain.model.ChannelSearchResult;
import br.com.harbsti.podtube.domain.model.VideoSearchResult;
import br.com.harbsti.podtube.domain.usecase.ChannelUseCase;
import rx.Observable;

/**
 * Created by marcosharbs on 05/12/16.
 */

public class ChannelUseCaseImpl implements ChannelUseCase {

    private ChannelRepository channelRepository;
    private ChannelDataMapper channelDataMapper;
    private ChannelDomainMapper channelDomainMapper;
    private ChannelSearchResultDomainMapper channelSearchResultDomainMapper;
    private VideoSearchResultDomainMapper videoSearchResultDomainMapper;

    public ChannelUseCaseImpl(ChannelRepository channelRepository,
                              ChannelDataMapper channelDataMapper,
                              ChannelDomainMapper channelDomainMapper,
                              ChannelSearchResultDomainMapper channelSearchResultDomainMapper,
                              VideoSearchResultDomainMapper videoSearchResultDomainMapper) {

        this.channelRepository = channelRepository;
        this.channelDataMapper = channelDataMapper;
        this.channelDomainMapper = channelDomainMapper;
        this.channelSearchResultDomainMapper = channelSearchResultDomainMapper;
        this.videoSearchResultDomainMapper = videoSearchResultDomainMapper;
    }

    @Override
    public Observable<ChannelSearchResult> getChannels(String term, String offset) {
        return channelRepository.getChannels(term, offset).compose(channelSearchResultDomainMapper.getTransformer());
    }

    @Override
    public Observable<Channel> getChannelDetail(Channel channel) {
        return channelRepository.getChannelDetail(channelDataMapper.transform(channel)).compose(channelDomainMapper.getTransformer());
    }

    @Override
    public Observable<VideoSearchResult> getChannelVideos(String playlistId, String offset) {
        return channelRepository.getVideos(playlistId, offset).compose(videoSearchResultDomainMapper.getTransformer());
    }

}
