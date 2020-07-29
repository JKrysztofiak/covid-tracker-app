package com.jkrysztofiak.covidtracker;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter{

    private Context context;
    private CountryStatsModel model;
    private ItemTouchHelper itemTouchHelper;

    public RecyclerViewAdapter(Context context, CountryStatsModel model) {
        this.context = context;
        this.model = model;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.w("YO!", "OnBindViewHolder called" );

//        holder.countryLabel.setText(countries.get(position).getCountry());
//        holder.newCasesLabel.setText(String.valueOf(countries.get(position).getNewCases()));
//        holder.totalCasesLabel.setText(String.valueOf(countries.get(position).getTotalCases()));

        holder.countryLabel.setText(model.getCountryAtIndex(position).getCountry());
        holder.newCasesLabel.setText(String.valueOf(model.getCountryAtIndex(position).getNewCases()));
        holder.totalCasesLabel.setText(String.valueOf(model.getCountryAtIndex(position).getTotalCases()));

        Log.w("YO!","On bind executed!");
    }

    @Override
    public int getItemCount() {
//        return countries.size();
        return model.getSize();
    }

    @Override
    public void onItemMove(int fromPosition, int toPostion) {
        model.swapCountries(fromPosition, toPostion);
        Log.w("YO!", "Swapped");
        notifyItemMoved(fromPosition, toPostion);
    }

    @Override
    public void onItemSwiped(int position) {
        Log.w("YO!", "Removing from: "+position+" out of "+model.getSize());
        model.removeCountry(position);
        notifyItemRemoved(position);
//        countries.remove(position);
//        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper){
        this.itemTouchHelper = touchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

        LinearLayout countryCard;
        TextView countryLabel;
        TextView newCasesLabel;
        TextView totalCasesLabel;

        GestureDetector gestureDetector;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            countryCard = (LinearLayout) itemView.findViewById(R.id.country_card);
            countryLabel = (TextView) itemView.findViewById(R.id.country_label);
            newCasesLabel = (TextView) itemView.findViewById(R.id.new_cases_number);
            totalCasesLabel = (TextView) itemView.findViewById(R.id.total_cases_number);

            gestureDetector = new GestureDetector(itemView.getContext(), this);

            itemView.setOnTouchListener(this);



        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            itemTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            gestureDetector.onTouchEvent(motionEvent);
            return true;
        }
    }
}
