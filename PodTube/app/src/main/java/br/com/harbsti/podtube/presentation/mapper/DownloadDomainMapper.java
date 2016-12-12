package br.com.harbsti.podtube.presentation.mapper;

import br.com.harbsti.podtube.domain.mapper.Mapper;
import br.com.harbsti.podtube.presentation.model.Download;

/**
 * Created by marcosharbs on 06/12/16.
 */

public class DownloadDomainMapper extends Mapper<Download, br.com.harbsti.podtube.domain.model.Download> {

    @Override
    public br.com.harbsti.podtube.domain.model.Download transform(Download download) {
        return br.com.harbsti.podtube.domain.model.Download.builder()
                .id(download.id())
                .image(download.image())
                .mp3File(download.mp3File())
                .pusblishDate(download.pusblishDate())
                .title(download.title())
                .build();
    }

}
