package br.com.harbsti.podtube.domain.mapper;

import br.com.harbsti.podtube.domain.model.VideoSearchResult;

/**
 * Created by marcosharbs on 05/12/16.
 */

public class VideoSearchResultDomainMapper extends Mapper<br.com.harbsti.podtube.data.model.VideoSearchResult, VideoSearchResult> {

    private VideoDomainMapper videoDomainMapper;

    public VideoSearchResultDomainMapper(VideoDomainMapper videoDomainMapper) {
        this.videoDomainMapper = videoDomainMapper;
    }

    public VideoSearchResult transform(br.com.harbsti.podtube.data.model.VideoSearchResult videoSearchResult){
        return VideoSearchResult.builder()
                .offset(videoSearchResult.offset())
                .videos(videoDomainMapper.transform(videoSearchResult.videos()))
                .build();
    }

}
