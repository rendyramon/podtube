package br.com.harbsti.podtube.presentation.mapper;

import br.com.harbsti.podtube.domain.mapper.Mapper;
import br.com.harbsti.podtube.presentation.model.Download;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class DownloadViewMapper extends Mapper<br.com.harbsti.podtube.domain.model.Download, Download> {

    @Override
    public Download transform(br.com.harbsti.podtube.domain.model.Download download) {
        return Download.builder()
                .id(download.id())
                .image(download.image())
                .mp3File(download.mp3File())
                .pusblishDate(download.pusblishDate())
                .title(download.title())
                .build();
    }

}
