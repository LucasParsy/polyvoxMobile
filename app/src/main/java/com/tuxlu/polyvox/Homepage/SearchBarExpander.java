package com.tuxlu.polyvox.Homepage;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.tuxlu.polyvox.R;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchBarExpander implements MenuItem.OnActionExpandListener {

    private final MenuItem searchItem;
    private final Menu menu;
    private final Activity activity;

    SearchBarExpander(MenuItem nIt, Menu nmen, Activity nact) {
        searchItem = nIt;
        menu = nmen;
        activity = nact;
    }

    @Override
    public boolean onMenuItemActionExpand(final MenuItem item) {
        setItemsVisibility(false);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(final MenuItem item) {
        setItemsVisibility(true);
        return true;
    }

    private void setItemsVisibility(final boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != searchItem)
                item.setVisible(visible);
        }
    }
}
