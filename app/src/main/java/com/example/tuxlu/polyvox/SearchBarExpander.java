package com.example.tuxlu.polyvox;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by parsyl on 24/07/2017.
 */

public class SearchBarExpander implements MenuItemCompat.OnActionExpandListener {

    private MenuItem searchItem;
    private Menu menu;

    SearchBarExpander(MenuItem nIt, Menu nmen) {
        searchItem = nIt;
        menu = nmen;
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
