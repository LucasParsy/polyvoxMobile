package com.tuxlu.polyvox.Homepage;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchResultsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }
}