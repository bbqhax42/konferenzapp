package com.example.chris.konferenz_app;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivityRecyclerAdapter extends RecyclerView.Adapter<MainActivityRecyclerAdapter.Holder> {
    Context context;
    List<Event> wordList;

    public MainActivityRecyclerAdapter(Context context, List<Event> wordList) {
        this.wordList = wordList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View item_list_view = LayoutInflater.from(context).inflate(R.layout.mainactivity_event_list, parent, false);


        return new Holder(item_list_view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        final Event event = wordList.get(position);
        holder.eventTitle.setText(event.getTitle());
        holder.start.setText(Config.formatDates(event.getStart()));
        holder.end.setText(Config.formatDates(event.getEnd()));
        holder.location.setText(event.getLocation());
        holder.cw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra("EventID", event.getEventId()); //goes to the selected event!!
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView eventTitle, start, end, location;
        CardView cw;

        public Holder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.title);
            start = (TextView) itemView.findViewById(R.id.starttime);
            end = (TextView) itemView.findViewById(R.id.endtime);
            location = (TextView) itemView.findViewById(R.id.location);

            cw = (CardView) itemView.findViewById(R.id.card_view);


        }
    }
}