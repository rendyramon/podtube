package br.com.harbsti.podtube.domain.mapper;

import br.com.harbsti.podtube.domain.model.Download;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class DownloadDataMapper extends Mapper<Download, br.com.harbsti.podtube.data.model.Download> {

    @Override
    public br.com.harbsti.podtube.data.model.Download transform(Download download) {
        return br.com.harbsti.podtube.data.model.Download.builder()
                .id(download.id())
                .image(download.image())
                .mp3File(download.mp3File())
                .pusblishDate(download.pusblishDate())
                .title(download.title())
                .build();
    }

}
