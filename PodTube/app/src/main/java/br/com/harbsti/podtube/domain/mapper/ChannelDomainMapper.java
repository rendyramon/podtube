package br.com.harbsti.podtube.domain.mapper;

import br.com.harbsti.podtube.domain.model.Channel;

/**
 * Created by marcosharbs on 05/12/16.
 */

public class ChannelDomainMapper extends Mapper<br.com.harbsti.podtube.data.model.Channel, Channel> {

    public Channel transform(br.com.harbsti.podtube.data.model.Channel channel) {
        return Channel.builder()
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
