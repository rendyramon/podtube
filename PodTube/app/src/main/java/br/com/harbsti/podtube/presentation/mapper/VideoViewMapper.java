package br.com.harbsti.podtube.presentation.mapper;

import br.com.harbsti.podtube.domain.mapper.Mapper;
import br.com.harbsti.podtube.presentation.model.Video;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class VideoViewMapper extends Mapper<br.com.harbsti.podtube.domain.model.Video, Video> {

    @Override
    public Video transform(br.com.harbsti.podtube.domain.model.Video video) {
        return Video.builder()
                .id(video.id())
                .image(video.image())
                .title(video.title())
                .pusblishDate(video.pusblishDate())
                .build();
    }

}
