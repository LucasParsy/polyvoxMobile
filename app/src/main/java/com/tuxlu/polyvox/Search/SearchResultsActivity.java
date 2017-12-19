package com.tuxlu.polyvox.Search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchResultsActivity extends ISearchResult {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.isEmpty())
                return;
            search(query);
        }
    }
}