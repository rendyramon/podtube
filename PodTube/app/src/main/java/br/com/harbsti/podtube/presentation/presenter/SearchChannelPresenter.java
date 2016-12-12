package br.com.harbsti.podtube.presentation.presenter;

/**
 * Created by marcosharbs on 05/12/16.
 */

public interface SearchChannelPresenter extends Presenter {

    void searchChannels(String term, String offset);

}
