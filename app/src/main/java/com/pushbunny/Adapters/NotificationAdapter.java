package com.pushbunny.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.pushbunny.GetTimeAgo;
import com.pushbunny.MainActivity;
import com.pushbunny.Models.NotificationModel;
import com.pushbunny.R;
import com.pushbunny.ViewMessageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<NotificationModel> list;
    public List<NotificationModel> itemsCopy = new ArrayList<>();
    public static List<NotificationModel> selectedItem = new ArrayList<>();
    public static int pos;
    public NotificationAdapter(Context context, List<NotificationModel> list){
        this.context = context;
        this.list = list;
        itemsCopy.addAll(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NotificationModel model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.time.setText(GetTimeAgo.getTimeAgo(Long.parseLong(model.getTime()),context));

        if (model.getOtherData().isEmpty() || model.getOtherData().equals("{}")){
            holder.subtitle.setVisibility(View.GONE);
        }else {
            holder.subtitle.setVisibility(View.VISIBLE);
        }

        try {
            JSONObject otherData = new JSONObject(model.getOtherData());
            holder.subtitle.setText(otherData.getString("subtitle"));

            if (otherData.getString("rtl").equals("1")){
                holder.title.setGravity(Gravity.LEFT);
                holder.title.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                holder.subtitle.setGravity(Gravity.LEFT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Delete Message");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Toast.makeText(context, holder.getAdapterPosition()+"", Toast.LENGTH_SHORT).show();
                                //return;
                                removeItem(holder.getAdapterPosition(), model);

                            }
                        });

                builder1.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                alert11.show();
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pos = holder.getAdapterPosition();
                selectedItem.clear();
                selectedItem.add(model);
                context.startActivity(new Intent(context, ViewMessageActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title, time, subtitle;
        private RelativeLayout layoutDirection;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
            subtitle = itemView.findViewById(R.id.subtitle);
            layoutDirection = itemView.findViewById(R.id.layoutDirection);
        }
    }

    public void removeItem(int position, NotificationModel model) {
        list.remove(position);
        itemsCopy.remove(model);
        Integer deletedRows = MainActivity.db.deleteData(model.getId());
        if (deletedRows > 0){
            //Toast.makeText(context, "Message Deletd", Toast.LENGTH_SHORT).show();
        }

        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(NotificationModel item, int position) {
        list.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void filter(String text) {
        list.clear();
        if(text.isEmpty()){
            Log.d("dxddd", itemsCopy.toString());
            list.addAll(itemsCopy);
            //list = itemsCopy;
        } else {
            text = text.toLowerCase();
            for(NotificationModel item: itemsCopy){
                if(item.getTitle().toLowerCase().contains(text)){
                    list.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
