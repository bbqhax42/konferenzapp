package com.example.chris.konferenz_app.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chris.konferenz_app.Config;
import com.example.chris.konferenz_app.data.Event;
import com.example.chris.konferenz_app.R;
import com.example.chris.konferenz_app.activities.EventActivity;

import java.util.List;

public class MainActivityRecyclerAdapter extends RecyclerView.Adapter<MainActivityRecyclerAdapter.Holder> {
    Context context;
    List<Event> eventList;

    public MainActivityRecyclerAdapter(Context context, List<Event> eventList) {
        this.eventList = eventList;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the xml/ view shell and return it

        View mainActivity_Event_List = LayoutInflater.from(context).inflate(R.layout.mainactivity_event_list, parent, false);
        return new Holder(mainActivity_Event_List);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        //populates the shell
        final Event event = eventList.get(position);
        holder.eventTitle.setText(event.getTitle());
        holder.start.setText(Config.formatDates(event.getStart()));
        holder.end.setText(Config.formatDates(event.getEnd()) + " Uhr");
        holder.location.setText(event.getStreet());
        holder.ort.setText(event.getCity());

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
        return eventList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView eventTitle, start, end, location, ort;
        CardView cw;

        public Holder(View itemView) {
            super(itemView);
            eventTitle = (TextView) itemView.findViewById(R.id.title);
            start = (TextView) itemView.findViewById(R.id.starttime);
            end = (TextView) itemView.findViewById(R.id.endtime);
            location = (TextView) itemView.findViewById(R.id.location);
            ort = (TextView) itemView.findViewById(R.id.ort);
            cw = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}