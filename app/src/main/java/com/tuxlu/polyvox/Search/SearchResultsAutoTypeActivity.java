package com.tuxlu.polyvox.Search;

import android.os.Bundle;
import android.support.v7.widget.SearchView;

import com.tuxlu.polyvox.R;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchResultsAutoTypeActivity extends ISearchResult implements SearchView.OnQueryTextListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SearchView)this.findViewById(R.id.searchView)).setOnQueryTextListener(this);
        //for search while typing.
    }


    public boolean onQueryTextChange(String query) {
        search(query);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}