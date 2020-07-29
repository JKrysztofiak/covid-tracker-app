package com.jkrysztofiak.covidtracker;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPostion);

    void onItemSwiped(int position);

}
