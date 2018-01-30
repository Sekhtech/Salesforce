package com.salesforce.nvisio.salesforce.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.salesforce.nvisio.salesforce.Model.job_model;
import com.salesforce.nvisio.salesforce.R;

import java.util.List;

/**
 * Created by USER on 09-May-17.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.activityViewHolder> {

    private List<job_model> list;
    private Context context;
    private LayoutInflater inflater;

    public ActivityAdapter(List<job_model> list, Context context) {
        this.list = list;
        this.context = context;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public activityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.logout_single,parent,false);
        return new activityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(activityViewHolder holder, int position) {
        job_model data=list.get(position);
        String startTime=data.getStart();
        String endTime=data.getEnd();
        holder.time.setText(startTime+" - "+endTime);
        holder.description.setText(data.getSubJob());
        holder.duration.setText(data.getDuration());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class activityViewHolder extends RecyclerView.ViewHolder{

        private TextView time,description,duration;
        public activityViewHolder(View itemView) {
            super(itemView);
            time= (TextView) itemView.findViewById(R.id.timetotime);
            description= (TextView) itemView.findViewById(R.id.description);
            duration= (TextView) itemView.findViewById(R.id.timeduration);
        }
    }
}
