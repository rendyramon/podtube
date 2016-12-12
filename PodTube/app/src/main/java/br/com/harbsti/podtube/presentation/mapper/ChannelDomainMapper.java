package br.com.harbsti.podtube.presentation.mapper;

import br.com.harbsti.podtube.domain.mapper.Mapper;
import br.com.harbsti.podtube.presentation.model.Channel;

/**
 * Created by marcosharbs on 05/12/16.
 */

public class ChannelDomainMapper extends Mapper<Channel, br.com.harbsti.podtube.domain.model.Channel> {

    public br.com.harbsti.podtube.domain.model.Channel transform(Channel channel) {
        return br.com.harbsti.podtube.domain.model.Channel.builder()
                .id(channel.id())
                .title(channel.title())
                .description(channel.description())
                .image(channel.image())
                .bannerImage(channel.bannerImage())
                .videosCount(channel.videosCount())
                .subscribersCount(channel.subscribersCount())
                .uploadsPlaylistName(channel.uploadsPlaylistName())
                .subscriptionId(channel.subscriptionId())
                .build();
    }

}
