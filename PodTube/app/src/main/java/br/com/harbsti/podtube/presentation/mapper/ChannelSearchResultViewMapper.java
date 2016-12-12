package br.com.harbsti.podtube.presentation.mapper;

import br.com.harbsti.podtube.domain.mapper.Mapper;
import br.com.harbsti.podtube.presentation.model.ChannelSearchResult;

/**
 * Created by marcosharbs on 05/12/16.
 */

public class ChannelSearchResultViewMapper extends Mapper<br.com.harbsti.podtube.domain.model.ChannelSearchResult, ChannelSearchResult> {

    private ChannelViewMapper channelMapper;

    public ChannelSearchResultViewMapper(ChannelViewMapper channelMapper) {
        this.channelMapper = channelMapper;
    }

    public ChannelSearchResult transform(br.com.harbsti.podtube.domain.model.ChannelSearchResult channelSearchResult){
        return ChannelSearchResult.builder()
                .offset(channelSearchResult.offset())
                .channels(channelMapper.transform(channelSearchResult.channels()))
                .build();
    }

}
