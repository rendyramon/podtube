package br.com.harbsti.podtube.presentation.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.wang.avi.AVLoadingIndicatorView;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import br.com.harbsti.podtube.R;
import br.com.harbsti.podtube.presentation.model.Video;
import br.com.harbsti.podtube.presentation.model.VideoSearchResult;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by marcosharbs on 30/11/16.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {

    private static final int VIEW_VIDEO = 0;
    private static final int VIEW_LOAD = 1;
    private static final int VIEW_HEADER = 2;

    private VideoSearchResult videoResult;
    private Context context;
    private VideosAdapter.OnVideosAdapter onVideosAdapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat formatter = new DecimalFormat("#,###,###");
    private BigInteger videosCount;

    public VideosAdapter(Context context, VideoSearchResult videoResult, BigInteger videosCount, VideosAdapter.OnVideosAdapter onVideosAdapter) {
        this.context = context;
        this.videoResult = videoResult;
        this.onVideosAdapter = onVideosAdapter;
        this.videosCount = videosCount;
    }

    public void appendVideoResult(VideoSearchResult videoResult) {
        this.videoResult = VideoSearchResult.builder()
                .offset(videoResult.offset())
                .videos(this.videoResult.videos())
                .build();
        this.videoResult.videos().addAll(videoResult.videos());
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_HEADER;
        } if(videoResult.offset() != null && position == videoResult.videos().size() + 1){
            return VIEW_LOAD;
        }
        return VIEW_VIDEO;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_VIDEO){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new VideosAdapter.ViewHolderVideo(view);
        }else if(viewType == VIEW_LOAD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load, parent, false);
            return new VideosAdapter.ViewHolderLoad(view);
        }else if(viewType == VIEW_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
            return new VideosAdapter.ViewHolderHeader(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(holder instanceof VideosAdapter.ViewHolderVideo) {
            final Video video = videoResult.videos().get(position-1);

            VideosAdapter.ViewHolderVideo holderVideo = (VideosAdapter.ViewHolderVideo) holder;
            holderVideo.imageVideo.setImageURI(ImageRequestBuilder.newBuilderWithSource(Uri.parse(video.image())).build().getSourceUri());
            holderVideo.textVideoName.setText(video.title());
            holderVideo.textVideoDate.setText(sdf.format(video.pusblishDate()));
            holderVideo.imageDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onVideosAdapter != null){
                        onVideosAdapter.onDownload(video);
                    }
                }
            });
            holderVideo.imageMovie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onVideosAdapter != null){
                        onVideosAdapter.onViewMovie(video);
                    }
                }
            });
        }else if(holder instanceof VideosAdapter.ViewHolderLoad) {
            final VideosAdapter.ViewHolderLoad holderLoad = (VideosAdapter.ViewHolderLoad) holder;
            holderLoad.labelLoadMore.setText(context.getString(R.string.label_loading_more));
            holderLoad.loadingIndicator.setVisibility(View.VISIBLE);
            onVideosAdapter.onLoadMore(videoResult.offset());
        }else if(holder instanceof VideosAdapter.ViewHolderHeader) {
            VideosAdapter.ViewHolderHeader holderHeader = (VideosAdapter.ViewHolderHeader) holder;

            holderHeader.labelHeader.setText(formatter.format(videosCount) + " " + context.getString(R.string.label_videos));
        }
    }

    @Override
    public int getItemCount() {
        return videoResult.videos().size() + (videoResult.offset() != null ? 2 : 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ViewHolderHeader extends VideosAdapter.ViewHolder {
        @InjectView(R.id.label_header) TextView labelHeader;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }


    class ViewHolderLoad extends VideosAdapter.ViewHolder {
        @InjectView(R.id.label_load_more) TextView labelLoadMore;
        @InjectView(R.id.loading_indicator) AVLoadingIndicatorView loadingIndicator;

        public ViewHolderLoad(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    class ViewHolderVideo extends VideosAdapter.ViewHolder {
        @InjectView(R.id.image_video) SimpleDraweeView imageVideo;
        @InjectView(R.id.text_video_name) TextView textVideoName;
        @InjectView(R.id.text_video_date) TextView textVideoDate;
        @InjectView(R.id.image_download) ImageView imageDownload;
        @InjectView(R.id.image_movie) ImageView imageMovie;

        public ViewHolderVideo(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    public interface OnVideosAdapter {
        void onLoadMore(String offset);
        void onDownload(Video video);
        void onViewMovie(Video video);
    }

}
